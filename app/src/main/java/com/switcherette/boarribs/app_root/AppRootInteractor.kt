package com.switcherette.boarribs.app_root

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.childaware.whenChildBuilt
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.minimal.reactive.Cancellable
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.badoo.ribs.routing.source.backstack.operation.pushOverlay
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.all_sightings_list.AllSightingsList
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.nav_bar.NavBar
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import io.reactivex.functions.Consumer

internal class AppRootInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<Configuration>,
    private val permissionRequester: PermissionRequester,
) : Interactor<AppRoot, AppRootView>(
    buildParams = buildParams
) {

    private lateinit var dialogLauncher: DialogLauncher
    private var cancellable: Cancellable? = null
    private val newSightingMapInputRelay = PublishRelay.create<NewSightingMap.Input>()
    private val newSightingFormInputRelay = PublishRelay.create<NewSightingForm.Input>()

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            dialogLauncher = node.integrationPoint.dialogLauncher
            bind(rib.input to Consumer { launchInputRequest() })
        }

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
                bind(child.output to allSightingsMapOutputConsumer)
            }
        }
        whenChildBuilt<NewSightingMap>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(newSightingMapInputRelay to child.input)
                bind(child.output to newSightingMapOutputConsumer)
            }
        }
        whenChildBuilt<NewSightingForm>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(newSightingFormInputRelay to child.input)
                bind(child.output to newSightingFormOutputConsumer)
            }
        }
    }

    override fun onViewCreated(view: AppRootView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            //bind(dialog.rx2() to dialogEventConsumer)
        }

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
                    NewSightingMap.Input.PermissionsGranted(event.granted)
                    //feature.accept(NewSightingMapFeature.Wish.UpdatePermissions(event.granted))
                }
                if (event.requestCode == REQUEST_CODE_CAMERA && event is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult) {
                    NewSightingForm.Input.PermissionsGranted(event.granted)
                    //feature.accept(NewSightingMapFeature.Wish.UpdatePermissions(event.granted))
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
                REQUEST_GEOLOCATION -> newSightingMapInputRelay.accept(NewSightingMap.Input.PermissionsGranted(
                    result.granted))
                REQUEST_CODE_CAMERA -> newSightingFormInputRelay.accept(NewSightingForm.Input.PermissionsGranted(
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
            NavBar.Output.MapButtonClicked -> backStack.push(Configuration.Content.AllSightingsMap)
            NavBar.Output.ListButtonClicked -> backStack.push(Configuration.Content.AllSightingsList)
            NavBar.Output.AddSightingButtonClicked -> backStack.push(Configuration.Content.NewSightingMap)
        }
    }

    private val allSightingsListOutputConsumer: Consumer<AllSightingsList.Output> = Consumer {
        when (it) {
            is AllSightingsList.Output.SightingSelected -> backStack.push(
                Configuration.Overlay.SightingDetails(
                    it.id
                )
            )
        }
    }

    private val allSightingsMapOutputConsumer: Consumer<AllSightingsMap.Output> = Consumer {
        when (it) {
            is AllSightingsMap.Output.SightingSelected -> backStack.pushOverlay(
                Configuration.Overlay.SightingDetails(
                    it.id
                )
            )
        }
    }

    private val newSightingMapOutputConsumer: Consumer<NewSightingMap.Output> = Consumer {
        when (it) {
            is NewSightingMap.Output.LocationAdded -> backStack.push(Configuration.Content.NewSightingForm(
                Coordinates(it.longitude, it.latitude)))
            is NewSightingMap.Output.PermissionsRequired -> requestPermissions(it.permissions,
                REQUEST_GEOLOCATION)
        }
    }

    private val newSightingFormOutputConsumer: Consumer<NewSightingForm.Output> = Consumer {
        when (it) {
            is NewSightingForm.Output.SightingAdded -> backStack.push(Configuration.Content.AllSightingsList)
            is NewSightingForm.Output.PermissionsRequired -> requestPermissions(it.permissions,
                REQUEST_CODE_CAMERA)
        }
    }

    /*
private val dialogEventConsumer: Consumer<SightingDetailsRibDialog.Event> = Consumer {
    when (it) {
        is SightingDetailsRibDialog.Event.NegativeButtonClicked -> dialogLauncher.hide(dialog)
        else -> {}
    }
}
 */

    companion object {
        private const val REQUEST_GEOLOCATION = 1
        private const val REQUEST_CODE_CAMERA = 2
    }
}
