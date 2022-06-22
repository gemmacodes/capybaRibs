package com.switcherette.boarribs.camera.mapper

import com.switcherette.boarribs.camera.Camera.Input
import com.switcherette.boarribs.camera.feature.CameraFeature.Wish

internal object InputToWish : (Input) -> Wish? {

    override fun invoke(input: Input): Wish? = when(input) {
        is Input.GrantPermissions -> Wish.UpdatePermissions(input.permissions)
    }
}
