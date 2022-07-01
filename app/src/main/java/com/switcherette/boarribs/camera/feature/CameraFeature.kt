package com.switcherette.boarribs.camera.feature

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.switcherette.boarribs.camera.feature.CameraFeature.*
import com.switcherette.boarribs.camera.feature.CameraFeature.Action.Execute
import io.reactivex.Observable

class CameraFeature() : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = { Execute(it) },
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val filepath: String? = null,
        val cameraPermissionStatus: CameraPermissionStatus? = null,
        val isCameraStarted: Boolean = false,
    ) {
        enum class CameraPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {
        object StartCameraIfReady : Wish()
        data class UpdatePermissions(val granted: List<String>) : Wish()
        data class TakePhoto(val uri: Uri?) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class PermissionsUpdated(val status: State.CameraPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
        object CameraStarted : Effect()
        data class PhotoTaken(val filepath: Uri?) : Effect()
    }

    sealed class News {
        data class PermissionsRequired(val permissions: List<String>) : News()
        data class PhotoTaken(val filepath: String) : News()
    }

    class ActorImpl : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> = when (action) {
            is Execute -> when (action.wish) {
                is Wish.UpdatePermissions -> handlePermissionResult(action.wish.granted)
                is Wish.StartCameraIfReady -> startCameraIfReady(state)
                is Wish.TakePhoto -> Observable.just(Effect.PhotoTaken(action.wish.uri))
            }
        }

        @SuppressLint("MissingPermission")
        private fun startCameraIfReady(state: State): Observable<Effect> {
            return if (state.cameraPermissionStatus != State.CameraPermissionStatus.GRANTED) {
                Observable.just(Effect.PermissionsNotGranted)
            } else {
                Observable.just(Effect.CameraStarted)
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


    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.PermissionsNotGranted -> state
            is Effect.PermissionsUpdated -> state.copy(cameraPermissionStatus = effect.status)
            is Effect.CameraStarted -> state.copy(isCameraStarted = true)
            is Effect.PhotoTaken -> state.copy(
                filepath = effect.filepath.toString(),
                isCameraStarted = false
            )
        }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? = when (effect) {
            is Effect.PermissionsUpdated -> {
                if (effect.status == State.CameraPermissionStatus.GRANTED)
                    Execute(Wish.StartCameraIfReady)
                else null
            }
            else -> null
        }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
            is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                IMAGE_CAPTURE_PERMISSIONS)
            is Effect.PhotoTaken -> News.PhotoTaken(effect.filepath.toString())
            else -> null
        }
    }


    companion object {
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}
