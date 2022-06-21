package com.switcherette.boarribs.camera.mapper

import com.switcherette.boarribs.camera.CameraView.Event
import com.switcherette.boarribs.camera.feature.CameraFeature.Wish

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Wish? =
        TODO("Implement CameraViewEventToWish mapping")
}
