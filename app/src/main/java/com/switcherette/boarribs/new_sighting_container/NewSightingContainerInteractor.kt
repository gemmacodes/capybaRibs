package com.switcherette.boarribs.new_sighting_container

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.childaware.whenChildBuilt
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.minimal.reactive.Cancellable
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.replace
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import io.reactivex.functions.Consumer

internal class NewSightingContainerInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<Configuration>,
    private val permissionRequester: PermissionRequester,
) : Interactor<NewSightingContainer, NewSightingContainerView>(
    buildParams = buildParams
) {

    private var cancellable: Cancellable? = null
    private val newSightingMapInputRelay = PublishRelay.create<NewSightingMap.Input>()
    private val newSightingFormInputRelay = PublishRelay.create<NewSightingForm.Input>()

    override fun onCreate(nodeLifecycle: Lifecycle) {

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

    override fun onViewCreated(view: NewSightingContainerView, viewLifecycle: Lifecycle) {
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
                }
                if (event.requestCode == REQUEST_IMAGE_ACCESS && event is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult) {
                    NewSightingForm.Input.PermissionsGranted(event.granted)
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
                REQUEST_IMAGE_ACCESS -> newSightingFormInputRelay.accept(NewSightingForm.Input.PermissionsGranted(
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

    private val newSightingMapOutputConsumer: Consumer<NewSightingMap.Output> = Consumer {
        when (it) {
            is NewSightingMap.Output.LocationAdded -> backStack.replace(Configuration.Content.NewSightingForm(
                it.coordinates))
            is NewSightingMap.Output.PermissionsRequired -> requestPermissions(it.permissions,
                REQUEST_GEOLOCATION)
        }
    }

    private val newSightingFormOutputConsumer: Consumer<NewSightingForm.Output> = Consumer {
        when (it) {
            is NewSightingForm.Output.SightingAdded -> NewSightingContainer.Output.SightingAdded
            is NewSightingForm.Output.PermissionsRequired -> requestPermissions(it.permissions,
                REQUEST_IMAGE_ACCESS)
        }
    }

    companion object {
        private const val REQUEST_GEOLOCATION = 1
        private const val REQUEST_IMAGE_ACCESS = 2
    }
}
