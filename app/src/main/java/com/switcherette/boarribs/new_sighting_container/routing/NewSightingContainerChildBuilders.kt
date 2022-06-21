package com.switcherette.boarribs.new_sighting_container.routing


import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.camera.CameraBuilder
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.NewSightingFormBuilder
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import com.switcherette.boarribs.new_sighting_map.NewSightingMapBuilder


internal class NewSightingContainerChildBuilders(
    dependency: NewSightingContainer.Dependency,
) {

    private val subtreeDependency: SubtreeDependency =
        SubtreeDependency(dependency)

    val newSightingMapBuilder = NewSightingMapBuilder(subtreeDependency)
    val newSightingFormBuilder = NewSightingFormBuilder(subtreeDependency)
    val cameraBuilder = CameraBuilder(subtreeDependency)

    class SubtreeDependency(
        dependency: NewSightingContainer.Dependency,
    ) : NewSightingContainer.Dependency by dependency,
        NewSightingMap.Dependency,
        NewSightingForm.Dependency,
        Camera.Dependency

}



