package com.switcherette.boarribs.new_sighting_container.mapper

import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import com.switcherette.boarribs.new_sighting_form.NewSightingForm


internal object FormOutputToContainerOutput : (NewSightingForm.Output) -> NewSightingContainer.Output? {
    override fun invoke(output: NewSightingForm.Output): NewSightingContainer.Output? =
        when(output){
            is NewSightingForm.Output.SightingAdded -> NewSightingContainer.Output.SightingAdded
            NewSightingForm.Output.CameraRequested -> null
        }

}