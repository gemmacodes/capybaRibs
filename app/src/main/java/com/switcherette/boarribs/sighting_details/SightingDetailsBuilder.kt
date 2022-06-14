package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature

class SightingDetailsBuilder(
    private val dependency: SightingDetails.Dependency
) : Builder<SightingDetails.Params, SightingDetails>() {

    override fun build(buildParams: BuildParams<SightingDetails.Params>): SightingDetails {
        val sightingId = buildParams.payload.sightingId
        val customisation = buildParams.getOrDefault(SightingDetails.Customisation())
        val feature = feature(dependency.sightingsDataSource, sightingId)
        val interactor = interactor(buildParams, feature)
        return node(buildParams, customisation, interactor, feature)
    }

    private fun feature(
        sightingsDataSource: SightingsDataSource,
        sightingId: String
    ) =
        SightingDetailsFeature(
        sightingsDataSource = sightingsDataSource,
        sightingId = sightingId)

    private fun interactor(
        buildParams: BuildParams<SightingDetails.Params>,
        feature: SightingDetailsFeature
    ) = SightingDetailsInteractor(
        buildParams = buildParams,
        feature = feature
    )

    private fun node(
        buildParams: BuildParams<SightingDetails.Params>,
        customisation: SightingDetails.Customisation,
        interactor: SightingDetailsInteractor,
        feature: SightingDetailsFeature
    ) = SightingDetailsNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, disposables(feature))
    )
}
