package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

class NewSightingFormBuilder(
    private val dependency: NewSightingForm.Dependency
) : Builder<NewSightingForm.Params, NewSightingForm>() {

    override fun build(buildParams: BuildParams<NewSightingForm.Params>): NewSightingForm {
        val latitude = buildParams.payload.latitude
        val longitude = buildParams.payload.longitude
        val customisation = buildParams.getOrDefault(NewSightingForm.Customisation())
        val feature = feature(dependency.sightingsDataSource, longitude, latitude)
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature(
        dataSource: SightingsDataSource,
        longitude: Double,
        latitude: Double
    ) =
        NewSightingFormFeature(dataSource, longitude, latitude)

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: NewSightingFormFeature
    ) = NewSightingFormInteractor(
        buildParams = buildParams,
        feature = feature
    )

    private fun node(
        buildParams: BuildParams<NewSightingForm.Params>,
        customisation: NewSightingForm.Customisation,
        feature: NewSightingFormFeature,
        interactor: NewSightingFormInteractor
    ) = NewSightingFormNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(interactor, disposables(feature))
    )
}
