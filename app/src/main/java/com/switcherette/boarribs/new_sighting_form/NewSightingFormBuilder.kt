package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

class NewSightingFormBuilder(
    private val dependency: NewSightingForm.Dependency
) : Builder<NewSightingForm.BuildParams, NewSightingForm>() {

    override fun build(buildParams: BuildParams<NewSightingForm.BuildParams>): NewSightingForm {
        val customisation = buildParams.getOrDefault(NewSightingForm.Customisation())
        val feature = feature(
            dependency.sightingsDataSource,
            buildParams.payload.coordinates.longitude,
            buildParams.payload.coordinates.latitude,
            dependency.activityStarter
        )
        val interactor = interactor(buildParams, feature)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature(
        dataSource: SightingsDataSource,
        longitude: Double,
        latitude: Double,
        activityStarter: ActivityStarter
    ) =
        NewSightingFormFeature(dataSource, longitude, latitude, activityStarter)

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: NewSightingFormFeature
    ) = NewSightingFormInteractor(
        buildParams = buildParams,
        feature = feature
    )

    private fun node(
        buildParams: BuildParams<NewSightingForm.BuildParams>,
        customisation: NewSightingForm.Customisation,
        feature: NewSightingFormFeature,
        interactor: NewSightingFormInteractor
    ) = NewSightingFormNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, disposables(feature))
    )
}
