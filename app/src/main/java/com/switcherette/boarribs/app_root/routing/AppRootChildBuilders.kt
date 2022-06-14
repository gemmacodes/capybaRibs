package com.switcherette.boarribs.app_root.routing

import com.switcherette.boarribs.all_sightings_list.AllSightingsList
import com.switcherette.boarribs.all_sightings_list.AllSightingsListBuilder
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapBuilder
import com.switcherette.boarribs.app_root.AppRoot
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl
import com.switcherette.boarribs.nav_bar.NavBar
import com.switcherette.boarribs.nav_bar.NavBarBuilder
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.NewSightingFormBuilder
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import com.switcherette.boarribs.new_sighting_map.NewSightingMapBuilder
import com.switcherette.boarribs.sighting_details.SightingDetails
import com.switcherette.boarribs.sighting_details.SightingDetailsBuilder

internal class AppRootChildBuilders(
    dependency: AppRoot.Dependency
) {
    private val subtreeDependency: SubtreeDependency = SubtreeDependency(dependency, SightingsDataSourceImpl)

    val navBarBuilder = NavBarBuilder(subtreeDependency)
    val newSightingMapBuilder = NewSightingMapBuilder(subtreeDependency)
    val newSightingFormBuilder = NewSightingFormBuilder(subtreeDependency)
    val allSightingsMapBuilder = AllSightingsMapBuilder(subtreeDependency)
    val allSightingsListBuilder = AllSightingsListBuilder(subtreeDependency)
    val sightingDetailsBuilder = SightingDetailsBuilder(subtreeDependency)


    class SubtreeDependency(
        dependency: AppRoot.Dependency,
        override val sightingsDataSource: SightingsDataSource,
    ) : AppRoot.Dependency by dependency,
        NavBar.Dependency,
        NewSightingMap.Dependency,
        NewSightingForm.Dependency,
        AllSightingsMap.Dependency,
        AllSightingsList.Dependency,
        SightingDetails.Dependency
}


