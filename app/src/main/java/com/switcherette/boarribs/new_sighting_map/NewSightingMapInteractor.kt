package com.switcherette.boarribs.new_sighting_map

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.permissionrequester.PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.minimal.reactive.Cancellable
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature
import com.switcherette.boarribs.new_sighting_map.mapper.InputToWish
import com.switcherette.boarribs.new_sighting_map.mapper.NewsToOutput
import com.switcherette.boarribs.new_sighting_map.mapper.StateToViewModel
import com.switcherette.boarribs.new_sighting_map.mapper.ViewEventToWish
import io.reactivex.functions.Consumer

internal class NewSightingMapInteractor(
    buildParams: BuildParams<*>,
    private val feature: NewSightingMapFeature
) : Interactor<NewSightingMap, NewSightingMapView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            bind(feature.news to rib.output using NewsToOutput)
            bind(rib.input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: NewSightingMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
        viewLifecycle.subscribe(
            onCreate = { feature.accept(NewSightingMapFeature.Wish.FindMyLocation) } ,
        )
    }

}

