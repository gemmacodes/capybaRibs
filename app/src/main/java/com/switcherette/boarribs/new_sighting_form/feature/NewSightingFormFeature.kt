package com.switcherette.boarribs.new_sighting_form.feature

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.requestcode.RequestCodeClient
import com.badoo.ribs.minimal.reactive.Source
import com.badoo.ribs.rx2.adapter.rx2
import com.switcherette.boarribs.BuildConfig
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.NewSightingFormView
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature.*
import io.reactivex.Observable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class NewSightingFormFeature(
    dataSource: SightingsDataSource,
    coordinates: Coordinates,
    activityStarter: ActivityStarter,
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(dataSource, coordinates, activityStarter),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
    postProcessor = PostProcessorImpl()
) {

    data class State(
        val id: String? = null,
        val heading: String? = null,
        val adults: Int? = null,
        val piglets: Int? = null,
        val interaction: Boolean = false,
        val comments: String? = null,
        val coordinates: Coordinates? = null,
        val timestamp: Long? = null,
        val picture: String? = null,
        val cameraPermissionStatus: CameraPermissionStatus? = null,
        val showDialog: Boolean = false,
    ) {
        enum class CameraPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {
        data class SaveSighting(
            val heading: String?,
            val adults: Int?,
            val piglets: Int?,
            val interaction: Boolean,
            val comments: String?,
        ) : Wish()

        object ShowImageSourceDialog : Wish()
        data class UpdatePhotoUri(val uri: String) : Wish()
        data class UpdatePermissions(val granted: List<String>) : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
    }

    sealed class Effect {
        object SightingSaved : Effect()
        data class PhotoUriUpdated(val uri: String) : Effect()
        data class PermissionsUpdated(val status: State.CameraPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
        object ImageSourceDialogShown : Effect()
    }

    sealed class News {
        object SightingSaved : News()
        data class PermissionsRequired(val permissions: List<String>) : News()
    }

    class ActorImpl(
        private val dataSource: SightingsDataSource,
        private val coordinates: Coordinates,
        private val activityStarter: ActivityStarter,
    ) : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> =
            when (action) {
                is Action.ExecuteWish -> when (action.wish) {
                    is Wish.SaveSighting -> saveForm(dataSource, action.wish, state)
                    is Wish.UpdatePhotoUri -> Observable.just(Effect.PhotoUriUpdated(action.wish.uri))
                    is Wish.UpdatePermissions -> handlePermissionResult(action.wish.granted)
                    is Wish.ShowImageSourceDialog -> Observable.just(Effect.ImageSourceDialogShown)
                }
            }

        private fun handlePermissionResult(permissionsGranted: List<String>): Observable<Effect> =
            if (permissionsGranted.containsAll(IMAGE_CAPTURE_PERMISSIONS)) {
                State.CameraPermissionStatus.GRANTED
            } else {
                State.CameraPermissionStatus.DENIED
            }.let { status ->
                Observable.just(Effect.PermissionsUpdated(status))
            }

        private fun saveForm(
            dataSource: SightingsDataSource,
            wish: Wish.SaveSighting,
            state: State
        ): Observable<Effect> {
            dataSource.saveSighting(
                Sighting(
                    id = UUID.randomUUID().toString(),
                    heading = wish.heading!!,
                    adults = wish.adults!!,
                    piglets = wish.piglets!!,
                    interaction = wish.interaction,
                    comments = wish.comments!!,
                    coordinates = coordinates,
                    timestamp = System.currentTimeMillis(),
                    picture = state.picture
                        ?: Uri.parse("android.resource://com.switcherette.boarribs/" + R.drawable.boar_img)
                            .toString()
                )
            )
            return Observable.just(Effect.SightingSaved)
        }

    }


    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is Effect.SightingSaved -> state
                is Effect.PhotoUriUpdated -> state.copy(picture = effect.uri)
                is Effect.PermissionsNotGranted -> state
                is Effect.PermissionsUpdated -> state.copy(cameraPermissionStatus = effect.status)
                is Effect.ImageSourceDialogShown -> state.copy(showDialog = true)
            }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? =
            when (effect) {
                Effect.SightingSaved -> News.SightingSaved
                is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                    IMAGE_CAPTURE_PERMISSIONS)
                is Effect.PermissionsUpdated -> null
                is Effect.PhotoUriUpdated -> null
                is Effect.ImageSourceDialogShown -> null
            }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? =
            when (effect) {
                is Effect.PermissionsUpdated -> {
                    if (effect.status == State.CameraPermissionStatus.GRANTED)
                        Action.ExecuteWish(Wish.ShowImageSourceDialog)
                    else null
                }
                else -> null
            }
    }

    companion object {
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}
