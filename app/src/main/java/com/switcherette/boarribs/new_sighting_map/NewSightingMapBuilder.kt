package com.switcherette.boarribs.new_sighting_map

import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature

class NewSightingMapBuilder(
    private val dependency: NewSightingMap.Dependency,
) : SimpleBuilder<NewSightingMap>() {

    override fun build(buildParams: BuildParams<Nothing?>): NewSightingMap {
        val customisation = buildParams.getOrDefault(NewSightingMap.Customisation())
        val feature = feature(dependency.locationClient)
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature(locationClient: FusedLocationProviderClient) =
        NewSightingMapFeature(locationClient)

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: NewSightingMapFeature,
    ) = NewSightingMapInteractor(
        buildParams = buildParams,
        feature = feature,
    )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: NewSightingMap.Customisation,
        feature: NewSightingMapFeature,
        interactor: NewSightingMapInteractor,
    ) = NewSightingMapNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, disposables(feature))
    )
}
