package com.switcherette.boarribs.new_sighting_map

import android.Manifest
import android.widget.Toast
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
import com.switcherette.boarribs.new_sighting_map.mapper.NewsToOutput
import com.switcherette.boarribs.new_sighting_map.mapper.StateToViewModel
import com.switcherette.boarribs.new_sighting_map.mapper.ViewEventToWish
import io.reactivex.functions.Consumer

internal class NewSightingMapInteractor(
    buildParams: BuildParams<*>,
    private val feature: NewSightingMapFeature,
    private val permissionRequester: PermissionRequester
) : Interactor<NewSightingMap, NewSightingMapView>(
    buildParams = buildParams
) {

    private var cancellable: Cancellable? = null

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            bind(feature.news to rib.output using NewsToOutput)
            bind( feature.news to featureNewsConsumer())
        }
    }

    override fun onViewCreated(view: NewSightingMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
        viewLifecycle.subscribe(
            onCreate = { handleOnCreate() },
            onDestroy = { handleOnDestroy() }
        )
    }

    private fun featureNewsConsumer(): Consumer<NewSightingMapFeature.News> = Consumer {
        when (it) {
            is NewSightingMapFeature.News.PermissionRequired -> requestPermissions(it.permissions)
            else -> {}
        }
    }

    private fun handleOnCreate() {
        cancellable = permissionRequester
            .events(this)
            .observe { event ->
                if (event.requestCode == REQUEST_GEOLOCATION && event is RequestPermissionsResult) {
                    feature.accept(NewSightingMapFeature.Wish.UpdatePermissions(event.granted))
                }
            }
        feature.accept(NewSightingMapFeature.Wish.FindMyLocation)
    }

    private fun handleOnDestroy() {
        cancellable?.cancel()
    }

    private fun requestPermissions(permissions:List<String>) {
        val result = permissionRequester.checkPermissions(
            client = this,
            permissions = permissions.toTypedArray()
        )
        if (result.allGranted){
            feature.accept(NewSightingMapFeature.Wish.UpdatePermissions(result.granted))
        } else {
            permissionRequester.requestPermissions(
                client = this,
                requestCode = REQUEST_GEOLOCATION,
                permissions = permissions.toTypedArray()
            )
        }
    }

    companion object {
        private const val REQUEST_GEOLOCATION = 1
    }

}

