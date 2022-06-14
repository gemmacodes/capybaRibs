package com.switcherette.boarribs.new_sighting_form.feature

import android.net.Uri
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature.*
import io.reactivex.Observable
import io.reactivex.Observable.empty
import java.util.*

internal class NewSightingFormFeature(
    dataSource: SightingsDataSource,
    longitude: Double,
    latitude: Double
) : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = State(),
    bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(dataSource, longitude, latitude),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val id: String = "TBD",
        val heading: String = "TBD",
        val adults: Int = 0,
        val piglets: Int = 0,
        val interaction: Boolean = false,
        val comments: String = "TBD",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val timestamp: Long = 0,
        val picture: String = "TBD"
    )

    sealed class Wish {
        data class SaveSighting(
            val heading: String?,
            val adults: Int?,
            val piglets: Int?,
            val interaction: Boolean,
            val comments: String?,
            val picture: String?
        ) : Wish()
    }

    sealed class Effect {
        object SightingSaved : Effect()
        object SightingIncomplete : Effect()
    }

    sealed class News {
        object SightingSaved: News()
        object SightingNotSaved: News()
    }

    class BootStrapperImpl : Bootstrapper<Wish> {
        override fun invoke(): Observable<Wish> =
            empty()
    }

    class ActorImpl(
        private val dataSource: SightingsDataSource,
        private val longitude: Double,
        private val latitude: Double
    ) : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when (wish) {
                is Wish.SaveSighting -> saveForm(dataSource, wish)
            }

        private fun saveForm(
            dataSource: SightingsDataSource,
            wish: Wish.SaveSighting
        ): Observable<Effect> {
            return if (
                wish.heading == null
                || wish.adults == null
                || wish.piglets == null
                || wish.comments == null
            ) {
                Observable.just(Effect.SightingIncomplete)
            } else {
                dataSource.saveSighting(
                    Sighting(
                        id = UUID.randomUUID().toString(),
                        heading = wish.heading,
                        adults = wish.adults,
                        piglets = wish.piglets,
                        interaction = wish.interaction,
                        comments = wish.comments,
                        latitude = latitude,
                        longitude = longitude,
                        timestamp = System.currentTimeMillis(),
                        picture = wish.picture
                            ?: Uri.parse("android.resource://com.switcherette.boarribs/" + R.drawable.boar_img)
                                .toString()
                    )
                )
                Observable.just(Effect.SightingSaved)
            }
        }
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                Effect.SightingSaved -> TODO()
                Effect.SightingIncomplete -> TODO()
            }
    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when (effect) {
                Effect.SightingIncomplete -> News.SightingNotSaved
                Effect.SightingSaved -> News.SightingSaved
            }
    }
}
