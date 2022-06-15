package com.switcherette.boarribs.all_sightings_map.feature

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.switcherette.boarribs.all_sightings_list.feature.AllSightingsListFeature
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature.*
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature
import io.reactivex.Observable
import io.reactivex.Observable.empty

internal class AllSightingsMapFeature(
    sightingsDataSource: SightingsDataSource,
) : BaseFeature<Wish, Action, Effect, State, Nothing>(
    initialState = State(content = State.Content.SightingsLoading),
    bootstrapper = BootStrapperImpl(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(sightingsDataSource),
    reducer = ReducerImpl()
) {

    data class State(
        val content: Content,
    ) {
        sealed class Content {
            object SightingsLoading : Content()
            data class SightingsLoaded(val sightings: List<Sighting>) : Content()
            object SightingLoadingError : Content()
        }
    }

    sealed class Wish {
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
        object LoadSightings : Action()
    }

    sealed class Effect {
        object LoadingStarted : Effect()
        data class SightingsLoaded(val sightings: List<Sighting>) : Effect()
        object LoadingFailed : Effect()
    }


    class BootStrapperImpl : Bootstrapper<Action> {
        override fun invoke(): Observable<Action> = init()
        private fun init(): Observable<Action> =
            Observable.just(Action.LoadSightings)
    }

    class ActorImpl(
        private val sightingsDataSource: SightingsDataSource
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is Action.LoadSightings ->
                    when(state.content){
                        is State.Content.SightingsLoaded -> Observable.empty()
                        else -> retrieveSightings()
                    }
                else -> empty()
            }

        private fun retrieveSightings(): Observable<Effect> =
            sightingsDataSource
                .loadSightings()
                .map<Effect> { sightings -> Effect.SightingsLoaded(sightings) }
                .onErrorReturnItem(Effect.LoadingFailed)
                .toObservable()
                .startWith(Effect.LoadingStarted)
    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =

            when (effect) {
                is Effect.SightingsLoaded -> state.copy(
                    content = State.Content.SightingsLoaded(sightings = effect.sightings)
                )
                is Effect.LoadingStarted -> state.copy(
                    content = State.Content.SightingsLoading
                )
                is Effect.LoadingFailed -> state.copy(
                    content = State.Content.SightingLoadingError
                )
                else -> TODO()
            }
    }
}

