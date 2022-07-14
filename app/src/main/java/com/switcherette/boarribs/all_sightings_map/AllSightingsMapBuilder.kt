package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature

class AllSightingsMapBuilder(
    private val dependency: AllSightingsMap.Dependency
) : SimpleBuilder<AllSightingsMap>() {

    override fun build(buildParams: BuildParams<Nothing?>): AllSightingsMap {
        val customisation = buildParams.getOrDefault(AllSightingsMap.Customisation())
        val feature = feature(dependency.sightingsDataSource, dependency.locationClient)
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature(
        dataSource: SightingsDataSource,
        locationClient: FusedLocationProviderClient) =
        AllSightingsMapFeature(dataSource, locationClient)

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: AllSightingsMapFeature
    ) = AllSightingsMapInteractor(
            buildParams = buildParams,
            feature = feature
        )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: AllSightingsMap.Customisation,
        feature: AllSightingsMapFeature,
        interactor: AllSightingsMapInteractor
    ) = AllSightingsMapNode(
            buildParams = buildParams,
            viewFactory = customisation.viewFactory,
            plugins = listOf(interactor, disposables(feature))
        )
}
