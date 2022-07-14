package com.switcherette.boarribs.all_sightings_map.feature

import android.Manifest
import android.annotation.SuppressLint
import com.badoo.mvicore.element.*
import com.badoo.mvicore.feature.BaseFeature
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature.*
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import io.reactivex.Observable
import io.reactivex.Observable.empty

internal class AllSightingsMapFeature(
    sightingsDataSource: SightingsDataSource,
    locationProviderClient: FusedLocationProviderClient,
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(content = State.Content.SightingsLoading),
    bootstrapper = BootStrapperImpl(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(sightingsDataSource, locationProviderClient),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
    postProcessor = PostProcessorImpl()
) {

    data class State(
        val content: Content,
        val userLocation: Coordinates = Coordinates(41.409428, 2.111255),
        val locationPermissionStatus: LocationPermissionStatus? = null,
    ) {
        sealed class Content {
            object SightingsLoading : Content()
            data class SightingsLoaded(val sightings: List<Sighting>) : Content()
            object SightingLoadingError : Content()
        }

        enum class LocationPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {
        data class UpdatePermissions(val granted: List<String>) : Wish()
        object FindMyLocation : Wish()
        data class UpdateUserLocation(val coordinates: Coordinates) : Wish()
        object SaveLocation : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
        object LoadSightings : Action()
    }

    sealed class Effect {
        object LoadingStarted : Effect()
        data class SightingsLoaded(val sightings: List<Sighting>) : Effect()
        object LoadingFailed : Effect()
        data class PermissionsUpdated(val status: State.LocationPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
        data class UserLocationUpdated(val coordinates: Coordinates) : Effect()
        data class LocationSaved(val coordinates: Coordinates) : Effect()
    }

    sealed class News {
        data class LocationSaved(val coordinates: Coordinates) : News()
        data class PermissionsRequired(val permissions: List<String>) : News()
    }


    class BootStrapperImpl : Bootstrapper<Action> {
        override fun invoke(): Observable<Action> = init()
        private fun init(): Observable<Action> =
            Observable.just(Action.LoadSightings)
    }

    class ActorImpl(
        private val sightingsDataSource: SightingsDataSource,
        private val locationProviderClient: FusedLocationProviderClient,
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is Action.LoadSightings ->
                    when (state.content) {
                        is State.Content.SightingsLoaded -> empty()
                        else -> retrieveSightings()
                    }
                is Action.ExecuteWish -> when (action.wish) {
                    is Wish.UpdatePermissions -> handlePermissionResult(action.wish.granted)
                    is Wish.FindMyLocation -> getMyLocation(state)
                    is Wish.SaveLocation -> Observable.just(
                        Effect.LocationSaved(state.userLocation))
                    is Wish.UpdateUserLocation -> Observable.just(
                        Effect.UserLocationUpdated(action.wish.coordinates))
                }
            }

        private fun retrieveSightings(): Observable<Effect> =
            sightingsDataSource
                .loadSightings()
                .map<Effect> { sightings -> Effect.SightingsLoaded(sightings) }
                .onErrorReturnItem(Effect.LoadingFailed)
                .toObservable()
                .startWith(Effect.LoadingStarted)

        private fun handlePermissionResult(
            permissionsGranted: List<String>,
        ): Observable<Effect> =
            if (permissionsGranted.containsAll(LOCATION_PERMISSIONS)) {
                State.LocationPermissionStatus.GRANTED
            } else {
                State.LocationPermissionStatus.DENIED
            }.let { status ->
                Observable.just(Effect.PermissionsUpdated(status))
            }


        @SuppressLint("MissingPermission")
        private fun getMyLocation(state: State): Observable<Effect> {
            return if (state.locationPermissionStatus != State.LocationPermissionStatus.GRANTED) {
                Observable.just(Effect.PermissionsNotGranted)
            } else {
                Observable.fromPublisher {
                    locationProviderClient.lastLocation.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            it.onNext(
                                Effect.UserLocationUpdated(
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
                is Effect.SightingsLoaded -> state.copy(
                    content = State.Content.SightingsLoaded(sightings = effect.sightings)
                )
                is Effect.LoadingStarted -> state.copy(
                    content = State.Content.SightingsLoading
                )
                is Effect.LoadingFailed -> state.copy(
                    content = State.Content.SightingLoadingError
                )
                is Effect.PermissionsUpdated -> state.copy(locationPermissionStatus = effect.status)
                is Effect.PermissionsNotGranted -> state
                is Effect.UserLocationUpdated -> state.copy(userLocation = effect.coordinates)
                is Effect.LocationSaved -> state.copy(userLocation = effect.coordinates)
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

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.LocationSaved -> News.LocationSaved(effect.coordinates)
                is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                    LOCATION_PERMISSIONS)
                else -> null
            }
    }

    companion object {
        private val LOCATION_PERMISSIONS = listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

