package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

class NewSightingFormBuilder(
    private val dependency: NewSightingForm.Dependency,
) : Builder<NewSightingForm.BuildParams, NewSightingForm>() {

    override fun build(buildParams: BuildParams<NewSightingForm.BuildParams>): NewSightingForm {
        val customisation = buildParams.getOrDefault(NewSightingForm.Customisation())
        val feature = feature(buildParams.payload.coordinates)
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature(
        coordinates: Coordinates,
    ) =
        NewSightingFormFeature(
            dependency.sightingsDataSource,
            coordinates,
            dependency.timeHelper,
            dependency.idHelper,
            dependency.defaultPictureUrl,
        )

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: NewSightingFormFeature,
    ) = NewSightingFormInteractor(
        buildParams = buildParams,
        feature = feature
    )

    private fun node(
        buildParams: BuildParams<NewSightingForm.BuildParams>,
        customisation: NewSightingForm.Customisation,
        feature: NewSightingFormFeature,
        interactor: NewSightingFormInteractor,
    ) = NewSightingFormNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, disposables(feature))
    )
}
