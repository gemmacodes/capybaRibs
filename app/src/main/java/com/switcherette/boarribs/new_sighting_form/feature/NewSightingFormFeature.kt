package com.switcherette.boarribs.new_sighting_form.feature

import android.net.Uri
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature.*
import io.reactivex.Observable
import java.util.*

internal class NewSightingFormFeature(
    dataSource: SightingsDataSource,
    coordinates: Coordinates
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(dataSource, coordinates),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
) {

    data class State(
        val id: String? = null,
        val heading: String? = null,
        val adults: Int? = null,
        val pups: Int? = null,
        val interaction: Boolean = false,
        val comments: String? = null,
        val coordinates: Coordinates? = null,
        val timestamp: Long? = null,
        val picture: String? = null,
    )

    sealed class Wish {
        data class SaveSighting(
            val heading: String?,
            val adults: String?,
            val pups: String?,
            val interaction: Boolean,
            val comments: String?,
        ) : Wish()

        object RequestCameraStart : Wish()
        data class UpdatePhotoUri(val uri: String) : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
    }

    sealed class Effect {
        object CameraStartRequested : Effect()
        object SightingSaved : Effect()
        object SightingNotSaved : Effect()
        data class PhotoUriUpdated(val uri: String) : Effect()
    }

    sealed class News {
        object CameraStarted : News()
        object SightingSaved : News()
        object SightingNotSaved : News()
    }

    class ActorImpl(
        private val dataSource: SightingsDataSource,
        private val coordinates: Coordinates,
    ) : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> =
            when (action) {
                is Action.ExecuteWish -> when (action.wish) {
                    is Wish.SaveSighting -> saveForm(dataSource, action.wish, state)
                    is Wish.UpdatePhotoUri -> Observable.just(Effect.PhotoUriUpdated(action.wish.uri))
                    is Wish.RequestCameraStart -> Observable.just(Effect.CameraStartRequested)
                }
            }

        private fun saveForm(
            dataSource: SightingsDataSource,
            wish: Wish.SaveSighting,
            state: State,
        ): Observable<Effect> {
            return if (
                wish.heading.isNullOrEmpty() || wish.adults.isNullOrEmpty() || wish.pups.isNullOrEmpty()
            ) {
                Observable.just(Effect.SightingNotSaved)
            } else {
                dataSource.saveSighting(
                    Sighting(
                        id = UUID.randomUUID().toString(),
                        heading = wish.heading,
                        adults = wish.adults.toInt(),
                        pups = wish.pups.toInt(),
                        interaction = wish.interaction,
                        comments = if (wish.comments.isNullOrEmpty()) "No comments" else wish.comments,
                        coordinates = coordinates,
                        timestamp = System.currentTimeMillis(),
                        picture = state.picture
                            ?: Uri.parse("android.resource://com.switcherette.boarribs/" + R.drawable.capybara)
                                .toString()
                    ))
                Observable.just(Effect.SightingSaved)
            }
        }
    }


    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is Effect.SightingSaved -> state
                is Effect.SightingNotSaved -> state
                is Effect.PhotoUriUpdated -> state.copy(picture = effect.uri)
                is Effect.CameraStartRequested -> state
            }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.SightingSaved -> News.SightingSaved
                is Effect.SightingNotSaved -> News.SightingNotSaved
                is Effect.PhotoUriUpdated -> null
                is Effect.CameraStartRequested -> News.CameraStarted
            }
    }


}
