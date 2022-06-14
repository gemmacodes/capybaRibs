package com.switcherette.boarribs.new_sighting_map

import android.Manifest
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.android.permissionrequester.PermissionRequester
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

    private var view: NewSightingMapView? = null
    private var cancellable: Cancellable? = null

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            bind(feature.news to rib.output using NewsToOutput)
        }
    }

    override fun onViewCreated(view: NewSightingMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
            bind(view to viewEventConsumer())
        }
        viewLifecycle.subscribe(
            onCreate = { handleOnCreate(view) },
            onDestroy = { handleOnDestroy() }
        )
    }

    private fun viewEventConsumer(): Consumer<NewSightingMapView.Event> = Consumer {
        when (it) {
            NewSightingMapView.Event.GetGeolocation -> requestPermissions()
            else -> {}
        }
    }

    private fun handleOnCreate(view: NewSightingMapView) {
        checkPermissions()
        this.view = view
        cancellable = permissionRequester
            .events(this)
            .observe { event ->
                if (event.requestCode == REQUEST_GEOLOCATION) {
                    when (event) {
                        is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult -> Toast.makeText(
                            view.context,
                            "Permission requested",
                            Toast.LENGTH_SHORT
                        ).show()
                        is PermissionRequester.RequestPermissionsEvent.Cancelled -> Toast.makeText(
                            view.context,
                            "Permission request cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                        else -> Unit
                    }
                }
            }
    }

    private fun handleOnDestroy() {
        this.view = null
        cancellable?.cancel()
    }

    private fun requestPermissions() {
        permissionRequester.requestPermissions(
            client = this,
            requestCode = REQUEST_GEOLOCATION,
            permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }

    private fun checkPermissions() {
        val result = permissionRequester.checkPermissions(
            client = this,
            permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        )
        if (!result.allGranted) requestPermissions()
        else {
            feature.accept(NewSightingMapFeature.Wish.StartGeolocation)
            Toast.makeText(
                view?.context,
                "Geolocation permissions already granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override val requestCodeClientId: String
        get() = this.toString()

    companion object {
        private const val REQUEST_GEOLOCATION = 1
    }


}

