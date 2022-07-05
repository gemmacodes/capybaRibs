package com.switcherette.boarribs.new_sighting_container.routing


import com.badoo.ribs.builder.Builder
import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.camera.CameraBuilder
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.NewSightingFormBuilder
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import com.switcherette.boarribs.new_sighting_map.NewSightingMapBuilder

interface NewSightingContainerChildBuilders {
    val newSightingMapBuilder: Builder<Nothing?, NewSightingMap>
    val newSightingFormBuilder: Builder<NewSightingForm.BuildParams, NewSightingForm>
    val cameraBuilder: Builder<Nothing?, Camera>
}

internal class NewSightingContainerChildBuildersImpl(
    dependency: NewSightingContainer.Dependency,
) : NewSightingContainerChildBuilders {

    private val subtreeDependency: SubtreeDependency =
        SubtreeDependency(dependency)

    override val newSightingMapBuilder: Builder<Nothing?, NewSightingMap> =
        NewSightingMapBuilder(subtreeDependency)
    override val newSightingFormBuilder: Builder<NewSightingForm.BuildParams, NewSightingForm> =
        NewSightingFormBuilder(subtreeDependency)
    override val cameraBuilder: Builder<Nothing?, Camera> = CameraBuilder(subtreeDependency)

    class SubtreeDependency(
        dependency: NewSightingContainer.Dependency,
    ) : NewSightingContainer.Dependency by dependency,
        NewSightingMap.Dependency,
        NewSightingForm.Dependency,
        Camera.Dependency

}



