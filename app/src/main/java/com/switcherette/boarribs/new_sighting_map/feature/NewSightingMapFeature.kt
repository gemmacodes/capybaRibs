package com.switcherette.boarribs.new_sighting_map.feature

import android.annotation.SuppressLint
import android.app.Activity
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.google.android.gms.location.LocationServices
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.*
import io.reactivex.Observable
import io.reactivex.Observable.empty

internal class NewSightingMapFeature : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = State(content = State.Content.BoarCoordinates(2.111255, 41.409428)),
    bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val content: Content,
    ) {
        sealed class Content {
            data class BoarCoordinates(val longitude: Double, val latitude: Double) : Content()
            object GeolocationError : Content()
        }
    }

    sealed class Wish {
        object StartGeolocation : Wish()
        data class SetPointerLocation(val longitude: Double, val latitude: Double) : Wish()
        data class SaveLocation(val longitude: Double, val latitude: Double) : Wish()
    }

    sealed class Effect {
        data class Geolocated(val longitude: Double, val latitude: Double) : Effect()
        data class PointerLocationSet(val longitude: Double, val latitude: Double) : Effect()
        data class LocationSaved(val longitude: Double, val latitude: Double) : Effect()
        object GeolocationFailed : Effect()
    }

    sealed class News {
        data class LocationSaved(val longitude: Double, val latitude: Double) : News()
    }

    class BootStrapperImpl : Bootstrapper<Wish> {
        override fun invoke(): Observable<Wish> =
            empty()
    }

    class ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when (wish) {
                Wish.StartGeolocation -> getGeolocation()
                is Wish.SaveLocation -> Observable.just(
                    Effect.LocationSaved(
                        wish.longitude,
                        wish.latitude
                    )
                )
                is Wish.SetPointerLocation -> Observable.just(
                    Effect.PointerLocationSet(
                        wish.longitude,
                        wish.latitude
                    )
                )
            }

        @SuppressLint("MissingPermission")
        fun getGeolocation(): Observable<Effect> {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(Activity())
            return if (fusedLocationClient.lastLocation.isSuccessful) {
                Observable.just(
                    Effect.Geolocated(
                        fusedLocationClient.lastLocation.result.longitude,
                        fusedLocationClient.lastLocation.result.latitude
                    )
                )
            } else Observable.just(Effect.GeolocationFailed)

        }
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is Effect.Geolocated -> state.copy(
                    content = State.Content.BoarCoordinates(
                        effect.longitude,
                        effect.latitude
                    )
                )
                is Effect.GeolocationFailed -> state.copy(content = State.Content.GeolocationError)
                is Effect.PointerLocationSet -> state.copy(
                    content = State.Content.BoarCoordinates(
                        effect.longitude,
                        effect.latitude
                    )
                )
                is Effect.LocationSaved -> state.copy(
                    content = State.Content.BoarCoordinates(
                        effect.longitude,
                        effect.latitude
                    )
                )
            }

    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.LocationSaved -> News.LocationSaved(effect.longitude, effect.latitude)
                else -> null
            }

    }

}


