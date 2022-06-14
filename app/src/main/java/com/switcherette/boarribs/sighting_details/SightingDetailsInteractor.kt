package com.switcherette.boarribs.sighting_details

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature
import com.switcherette.boarribs.sighting_details.mapper.StateToViewModel

internal class SightingDetailsInteractor(
    buildParams: BuildParams<SightingDetails.BuildParams>,
    private val feature: SightingDetailsFeature
) : Interactor<SightingDetails, SightingDetailsView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
        }
    }

    override fun onViewCreated(view: SightingDetailsView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
        }
    }
}
