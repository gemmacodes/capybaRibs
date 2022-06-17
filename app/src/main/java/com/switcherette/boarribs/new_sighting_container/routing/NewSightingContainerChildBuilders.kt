package com.switcherette.boarribs.new_sighting_container.routing


import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    class SubtreeDependency(
        dependency: NewSightingContainer.Dependency,
    ) : NewSightingContainer.Dependency by dependency,
        NewSightingMap.Dependency,
        NewSightingForm.Dependency

}



