package com.switcherette.boarribs.app_root

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.childaware.whenChildBuilt
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.minimal.reactive.Cancellable
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.pushOverlay
import com.badoo.ribs.routing.source.backstack.operation.replace
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.all_sightings_list.AllSightingsList
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import com.switcherette.boarribs.nav_bar.NavBar
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import io.reactivex.functions.Consumer

internal class AppRootInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<Configuration>,
    private val permissionRequester: PermissionRequester,
) : Interactor<AppRoot, AppRootView>(
    buildParams = buildParams
) {

    private var cancellable: Cancellable? = null
    private val allSightingsMapInputRelay = PublishRelay.create<AllSightingsMap.Input>()

    override fun onCreate(nodeLifecycle: Lifecycle) {

        whenChildBuilt<NavBar>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to navBarOutputConsumer)
            }
        }
        whenChildBuilt<AllSightingsList>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to allSightingsListOutputConsumer)
            }
        }
        whenChildBuilt<AllSightingsMap>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(allSightingsMapInputRelay to child.input)
                bind(child.output to allSightingsMapOutputConsumer)
            }
        }
        whenChildBuilt<NewSightingContainer>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to newSightingContainerOutputConsumer)
            }
        }
    }

    override fun onViewCreated(view: AppRootView, viewLifecycle: Lifecycle) {
        viewLifecycle.subscribe(
            onCreate = { handleOnCreate() },
            onDestroy = { handleOnDestroy() }
        )
    }

    private fun handleOnCreate() {
        cancellable = permissionRequester
            .events(this)
            .observe { event ->
                if (event.requestCode == REQUEST_GEOLOCATION && event is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult) {
                    allSightingsMapInputRelay.accept(AllSightingsMap.Input.GrantPermissions(event.granted))
                }
            }
    }

    private fun handleOnDestroy() {
        cancellable?.cancel()
    }

    private fun requestPermissions(permissions: List<String>, requestCode: Int) {
        val result = permissionRequester.checkPermissions(
            client = this,
            permissions = permissions.toTypedArray()
        )
        if (result.allGranted) {
            when (requestCode) {
                REQUEST_GEOLOCATION -> allSightingsMapInputRelay.accept(
                    AllSightingsMap.Input.GrantPermissions(
                        result.granted))
            }
        } else {
            permissionRequester.requestPermissions(
                client = this,
                requestCode = requestCode,
                permissions = permissions.toTypedArray()
            )
        }
    }

    private val navBarOutputConsumer: Consumer<NavBar.Output> = Consumer {
        when (it) {
            NavBar.Output.MapDisplayRequested -> backStack.replace(Configuration.Content.AllSightingsMap)
            NavBar.Output.ListDisplayRequested -> backStack.replace(Configuration.Content.AllSightingsList)
            NavBar.Output.AddNewSightingRequested -> backStack.replace(Configuration.Content.NewSightingContainer)
        }
    }

    private val allSightingsListOutputConsumer: Consumer<AllSightingsList.Output> = Consumer {
        when (it) {
            is AllSightingsList.Output.SightingDetailsRequested -> {
                backStack.pushOverlay(Configuration.Overlay.SightingDetails(it.id))
            }
        }
    }

    private val allSightingsMapOutputConsumer: Consumer<AllSightingsMap.Output> = Consumer {
        when (it) {
            is AllSightingsMap.Output.SightingDetailsRequested ->
                backStack.pushOverlay(Configuration.Overlay.SightingDetails(it.id))
            is AllSightingsMap.Output.LocationAdded -> {}
            is AllSightingsMap.Output.PermissionsRequired -> requestPermissions(it.permissions,
                REQUEST_GEOLOCATION)
        }
    }

    private val newSightingContainerOutputConsumer: Consumer<NewSightingContainer.Output> =
        Consumer {
            when (it) {
                is NewSightingContainer.Output.SightingAdded ->
                    backStack.replace(Configuration.Content.AllSightingsList)
            }
        }


    companion object {
        private const val REQUEST_GEOLOCATION = 1
    }


}
