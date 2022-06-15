package com.switcherette.boarribs.sighting_details.feature

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature.*
import io.reactivex.Observable
import io.reactivex.Observable.empty

internal class SightingDetailsFeature(
    sightingsDataSource: SightingsDataSource,
    sightingId: String
) : BaseFeature<Wish, Action, Effect, State, Nothing>(
    initialState = State(content = State.Content.SightingLoading),
    bootstrapper = BootStrapperImpl(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(sightingsDataSource, sightingId),
    reducer = ReducerImpl(),
) {

    data class State(
        val content: Content,
    ) {
        sealed class Content {
            object SightingLoading : Content()
            data class SightingLoaded(val sighting: Sighting?) : Content()
            object SightingLoadingError : Content()
        }
    }

    sealed class Wish {
        object RetrySightingLoading : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
        object LoadSighting : Action()
    }

    sealed class Effect {
        object LoadingStarted : Effect()
        data class SightingLoaded(val sighting: Sighting) : Effect()
        object LoadingFailed : Effect()
    }


    class BootStrapperImpl : Bootstrapper<Action> {
        override fun invoke(): Observable<Action> = init()
        private fun init(): Observable<Action> =
            Observable.just(Action.LoadSighting)
    }

    class ActorImpl(
        private val sightingsDataSource: SightingsDataSource,
        private val sightingId: String
    ) : Actor<State, Action, Effect> {

        override fun invoke(
            state: State,
            action: Action
        ): Observable<out Effect> =
            when (action) {
                is Action.LoadSighting ->
                    when (state.content) {
                        is State.Content.SightingLoaded -> Observable.empty()
                        else -> retrieveSightings()
                    }
                else -> empty()
            }

        private fun retrieveSightings(): Observable<Effect> =
            sightingsDataSource
                .loadSighting(sightingId)
                .map<Effect> { sighting -> Effect.SightingLoaded(sighting) }
                .onErrorReturnItem(Effect.LoadingFailed)
                .toObservable()
                .startWith(Effect.LoadingStarted)
    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(
            state: State,
            effect: Effect
        ): State =

            when (effect) {
                is Effect.SightingLoaded -> state.copy(
                    content = State.Content.SightingLoaded(sighting = effect.sighting)
                )
                is Effect.LoadingStarted -> state.copy(
                    content = State.Content.SightingLoading
                )
                is Effect.LoadingFailed -> state.copy(
                    content = State.Content.SightingLoadingError
                )
            }
    }
}
