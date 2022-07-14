package com.switcherette.boarribs.new_sighting_map.feature

import android.Manifest
import android.annotation.SuppressLint
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.*
import io.reactivex.Observable
import io.reactivex.Observable.just

internal class NewSightingMapFeature(
    locationProviderClient: FusedLocationProviderClient,
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(locationProviderClient),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
    postProcessor = PostProcessorImpl()
) {

    data class State(
        val boarLocation: Coordinates = Coordinates(41.409428, 2.111255),
        val locationPermissionStatus: LocationPermissionStatus? = null,
    ) {
        enum class LocationPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {
        data class UpdatePermissions(val granted: List<String>) : Wish()
        object FindMyLocation : Wish()
        data class UpdateBoarLocation(val coordinates: Coordinates) : Wish()
        object SaveLocation : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class PermissionsUpdated(val status: State.LocationPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
        data class BoarLocationUpdated(val coordinates: Coordinates) : Effect()
        data class LocationSaved(val coordinates: Coordinates) : Effect()

    }

    sealed class News {
        data class LocationSaved(val coordinates: Coordinates) : News()
        data class PermissionsRequired(val permissions: List<String>) : News()
    }

    class ActorImpl(
        private val locationProviderClient: FusedLocationProviderClient,
    ) : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> =
            when (action) {
                is Action.ExecuteWish -> when (action.wish) {
                    is Wish.UpdatePermissions -> handlePermissionResult(action.wish.granted)
                    is Wish.FindMyLocation -> getMyLocation(state)
                    is Wish.SaveLocation -> just(Effect.LocationSaved(state.boarLocation))
                    is Wish.UpdateBoarLocation -> just(Effect.BoarLocationUpdated(action.wish.coordinates))
                }
            }

        private fun handlePermissionResult(
            permissionsGranted: List<String>,
        ): Observable<Effect> =
            if (permissionsGranted.containsAll(LOCATION_PERMISSIONS)) {
                State.LocationPermissionStatus.GRANTED
            } else {
                State.LocationPermissionStatus.DENIED
            }.let { status ->
                just(Effect.PermissionsUpdated(status))
            }


        @SuppressLint("MissingPermission")
        private fun getMyLocation(state: State): Observable<Effect> {
            return if (state.locationPermissionStatus != State.LocationPermissionStatus.GRANTED) {
                just(Effect.PermissionsNotGranted)
            } else {
                Observable.fromPublisher {
                    locationProviderClient.lastLocation.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            it.onNext(
                                Effect.BoarLocationUpdated(
                                    Coordinates(
                                        result.result.latitude,
                                        result.result.longitude,
                                    )
                                ))
                        }
                        it.onComplete()
                    }
                }
            }
        }

    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is Effect.PermissionsUpdated -> state.copy(locationPermissionStatus = effect.status)
                is Effect.PermissionsNotGranted -> state
                is Effect.BoarLocationUpdated -> state.copy(boarLocation = effect.coordinates)
                is Effect.LocationSaved -> state.copy(boarLocation = effect.coordinates)
            }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.LocationSaved -> News.LocationSaved(effect.coordinates)
                is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                    LOCATION_PERMISSIONS)
                else -> null
            }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? =
            when (effect) {
                is Effect.PermissionsUpdated -> {
                    if (effect.status == State.LocationPermissionStatus.GRANTED)
                        Action.ExecuteWish(Wish.FindMyLocation)
                    else null
                }
                else -> null
            }
    }

    companion object {
        private val LOCATION_PERMISSIONS = listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

}




