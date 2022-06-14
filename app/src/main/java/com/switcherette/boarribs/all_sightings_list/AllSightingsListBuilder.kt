package com.switcherette.boarribs.all_sightings_list

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.all_sightings_list.feature.AllSightingsListFeature

class AllSightingsListBuilder(
    private val dependency: AllSightingsList.Dependency
) : SimpleBuilder<AllSightingsList>() {

    override fun build(buildParams: BuildParams<Nothing?>): AllSightingsList {
        val customisation = buildParams.getOrDefault(AllSightingsList.Customisation())
        val feature = feature()
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature() =
        AllSightingsListFeature(dependency.sightingsDataSource)

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: AllSightingsListFeature
    ) = AllSightingsListInteractor(
            buildParams = buildParams,
            feature = feature
        )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: AllSightingsList.Customisation,
        feature: AllSightingsListFeature,
        interactor: AllSightingsListInteractor
    ) = AllSightingsListNode(
            buildParams = buildParams,
            viewFactory = customisation.viewFactory,
            plugins = listOf(interactor, disposables(feature))
        )
}
