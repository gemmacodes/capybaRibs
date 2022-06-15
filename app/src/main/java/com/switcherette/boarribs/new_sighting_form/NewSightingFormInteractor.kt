package com.switcherette.boarribs.new_sighting_form

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import com.switcherette.boarribs.new_sighting_form.mapper.NewsToOutput
import com.switcherette.boarribs.new_sighting_form.mapper.StateToViewModel
import com.switcherette.boarribs.new_sighting_form.mapper.ViewEventToWish
import com.switcherette.boarribs.new_sighting_form.mapper.InputToWish

internal class NewSightingFormInteractor(
    buildParams: BuildParams<*>,
    private val feature: NewSightingFormFeature
) : Interactor<NewSightingForm, NewSightingFormView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
           bind(feature.news to rib.output using NewsToOutput)
            bind(rib.input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: NewSightingFormView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
           bind(feature to view using StateToViewModel)
           bind(view to feature using ViewEventToWish)
        }
    }


}
