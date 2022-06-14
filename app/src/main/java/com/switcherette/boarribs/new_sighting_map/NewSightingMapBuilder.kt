package com.switcherette.boarribs.new_sighting_map

import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature

class NewSightingMapBuilder(
    private val dependency: NewSightingMap.Dependency
) : SimpleBuilder<NewSightingMap>() {

    override fun build(buildParams: BuildParams<Nothing?>): NewSightingMap {
        val customisation = buildParams.getOrDefault(NewSightingMap.Customisation())
        val feature = feature()
        val interactor = interactor(buildParams, feature, dependency.permissionRequester)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature() =
        NewSightingMapFeature()

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: NewSightingMapFeature,
        permissionRequester: PermissionRequester
    ) = NewSightingMapInteractor(
            buildParams = buildParams,
            feature = feature,
            permissionRequester = permissionRequester
        )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: NewSightingMap.Customisation,
        feature: NewSightingMapFeature,
        interactor: NewSightingMapInteractor
    ) = NewSightingMapNode(
            buildParams = buildParams,
            viewFactory = customisation.viewFactory,
            plugins = listOf(interactor, disposables(feature))
        )
}
