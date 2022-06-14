package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature

class AllSightingsMapBuilder(
    private val dependency: AllSightingsMap.Dependency
) : SimpleBuilder<AllSightingsMap>() {

    override fun build(buildParams: BuildParams<Nothing?>): AllSightingsMap {
        val customisation = buildParams.getOrDefault(AllSightingsMap.Customisation())
        val feature = feature()
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature() =
        AllSightingsMapFeature(dependency.sightingsDataSource)

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
