package com.switcherette.boarribs.camera.mapper

import com.switcherette.boarribs.camera.CameraView.ViewModel
import com.switcherette.boarribs.camera.feature.CameraFeature.State

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        TODO("Implement StateToViewModel mapping")
}
