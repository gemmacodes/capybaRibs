package com.switcherette.boarribs.camera.feature

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import com.badoo.mvicore.element.*
import com.badoo.mvicore.feature.BaseFeature
import com.switcherette.boarribs.camera.feature.CameraFeature.*
import com.switcherette.boarribs.camera.feature.CameraFeature.Action.Execute
import com.switcherette.boarribs.camera.takePhoto
import io.reactivex.Observable

class CameraFeature : BaseFeature<Wish, Action, Effect, State, News>(
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
        val isCameraStarted: Boolean = false
    ) {
        enum class CameraPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {
        object OpenCameraIfReady : Wish()
        data class UpdatePermissions(val granted: List<String>) : Wish()
        object TakePhoto : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class PermissionsUpdated(val status: State.CameraPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
        object CameraOpened: Effect()
        object PhotoTaken : Effect()
    }

    sealed class News {
        data class PermissionsRequired(val permissions: List<String>) : News()
    }

    class ActorImpl : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> = when (action) {
            is Execute -> when (action.wish){
                is Wish.UpdatePermissions -> handlePermissionResult(action.wish.granted)
                is Wish.OpenCameraIfReady -> openCameraIfReady(state)
                is Wish.TakePhoto -> launchTakePhoto()
            }
        }

        private fun launchTakePhoto(): Observable<CameraFeature.Effect> {
            return Observable.just(Effect.PhotoTaken) //TODO: Does camera logic go here?
        }

        @SuppressLint("MissingPermission")
        private fun openCameraIfReady(state: State): Observable<Effect> {
            return if (state.cameraPermissionStatus != State.CameraPermissionStatus.GRANTED) {
                Observable.just(Effect.PermissionsNotGranted)
            } else {
                launchTakePhoto()
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
            is Effect.CameraOpened -> state.copy(isCameraStarted = true)
            is Effect.PhotoTaken -> state
        }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? = when (effect) {
            is Effect.PermissionsUpdated -> {
                if (effect.status == State.CameraPermissionStatus.GRANTED)
                    Execute(Wish.OpenCameraIfReady)
                    else null
            }
            else -> null
        }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
            is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                IMAGE_CAPTURE_PERMISSIONS)
            else -> null
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}
