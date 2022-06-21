package com.switcherette.boarribs.camera.mapper

import com.switcherette.boarribs.camera.Camera.Input
import com.switcherette.boarribs.camera.feature.CameraFeature.Wish

internal object InputToWish : (Input) -> Wish? {

    override fun invoke(event: Input): Wish? =
        TODO("Implement CameraInputToWish mapping")
}
