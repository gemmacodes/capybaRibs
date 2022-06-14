package com.switcherette.boarribs.all_sightings_map

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.binder.using
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature
import com.switcherette.boarribs.all_sightings_map.mapper.StateToViewModel
import com.switcherette.boarribs.all_sightings_map.mapper.ViewEventToOutput

internal class AllSightingsMapInteractor(
    buildParams: BuildParams<*>,
    private val feature: AllSightingsMapFeature
) : Interactor<AllSightingsMap, AllSightingsMapView>(
    buildParams = buildParams
) {

    private lateinit var dialogLauncher: DialogLauncher

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            dialogLauncher = node.integrationPoint.dialogLauncher
        }
    }

    override fun onViewCreated(view: AllSightingsMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to rib.output using ViewEventToOutput)
            bind(feature to view using StateToViewModel)
        }
    }
}
