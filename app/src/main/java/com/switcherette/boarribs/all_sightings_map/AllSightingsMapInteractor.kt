package com.switcherette.boarribs.all_sightings_map

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.binder.using
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature.*
import com.switcherette.boarribs.all_sightings_map.mapper.StateToViewModel
import com.switcherette.boarribs.all_sightings_map.mapper.ViewEventToOutput
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature
import com.switcherette.boarribs.new_sighting_map.mapper.InputToWish
import com.switcherette.boarribs.new_sighting_map.mapper.NewsToOutput

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
            bind(feature.news to rib.output using com.switcherette.boarribs.all_sightings_map.mapper.NewsToOutput)
            bind(rib.input to feature using com.switcherette.boarribs.all_sightings_map.mapper.InputToWish)
        }
    }

    override fun onViewCreated(view: AllSightingsMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to rib.output using ViewEventToOutput)
            bind(feature to view using StateToViewModel)
        }
        viewLifecycle.subscribe(
            onCreate = { feature.accept(Wish.FindMyLocation) }
        )
    }
}
