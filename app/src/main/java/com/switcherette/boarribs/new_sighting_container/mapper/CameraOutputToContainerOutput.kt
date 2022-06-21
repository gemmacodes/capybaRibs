package com.switcherette.boarribs.new_sighting_container.mapper

import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import com.switcherette.boarribs.new_sighting_form.NewSightingForm


internal object CameraOutputToContainerOutput : (Camera.Output) -> NewSightingContainer.Output? {
    override fun invoke(output: Camera.Output): NewSightingContainer.Output? =
        when(output){
            is Camera.Output.PermissionsRequired -> null
            is Camera.Output.PhotoTaken -> null
        }

}