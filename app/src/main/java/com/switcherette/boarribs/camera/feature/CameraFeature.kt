package com.switcherette.boarribs.camera.feature

import android.Manifest
import android.annotation.SuppressLint
import com.badoo.mvicore.element.*
import com.badoo.mvicore.feature.BaseFeature
import com.switcherette.boarribs.camera.feature.CameraFeature.*
import com.switcherette.boarribs.camera.feature.CameraFeature.Action.Execute
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature
import io.reactivex.Observable

class CameraFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = { Execute(it) },
    bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val filepath: String? = null,
        val cameraPermissionStatus: CameraPermissionStatus? = null,
    ) {
        enum class CameraPermissionStatus { GRANTED, DENIED }
    }

    sealed class Wish {

    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
        object StartCamera : Action()
        data class UpdatePermissions(val granted: List<String>) : Action()
    }

    sealed class Effect {
        data class PermissionsUpdated(val status: State.CameraPermissionStatus) : Effect()
        object PermissionsNotGranted : Effect()
    }

    sealed class News {
        data class PermissionsRequired(val permissions: List<String>) : News()
    }

    class BootStrapperImpl : Bootstrapper<Action> {
        override fun invoke(): Observable<Action> =
            Observable.just(Action.StartCamera)

    }

    class ActorImpl : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> = when (action) {
            is Action.StartCamera -> takePhoto(state)
            is Action.UpdatePermissions -> handlePermissionResult(action.granted)
            is Execute -> Observable.empty()
        }

        @SuppressLint("MissingPermission")
        private fun takePhoto(state: State): Observable<Effect> {
            return if (state.cameraPermissionStatus != State.CameraPermissionStatus.GRANTED) {
                Observable.just(Effect.PermissionsNotGranted)
            } else {
                TODO()
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
        }
    }

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? = when (effect) {
            is Effect.PermissionsUpdated -> {
                if (effect.status == State.CameraPermissionStatus.GRANTED)
                    Action.StartCamera
                    else null
            }
            else -> null
        }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
            is Effect.PermissionsNotGranted -> News.PermissionsRequired(
                IMAGE_CAPTURE_PERMISSIONS)
            is Effect.PermissionsUpdated -> null
        }
    }

    companion object {
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}
