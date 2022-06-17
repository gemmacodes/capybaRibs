package com.switcherette.boarribs.app_root

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.childaware.whenChildBuilt
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.minimal.reactive.Cancellable
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.pushOverlay
import com.badoo.ribs.routing.source.backstack.operation.replace
import com.badoo.ribs.rx2.adapter.rx2
import com.switcherette.boarribs.all_sightings_list.AllSightingsList
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import com.switcherette.boarribs.nav_bar.NavBar
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer
import com.switcherette.boarribs.sighting_details.SightingDetails
import com.switcherette.boarribs.sighting_details.dialog.SightingDetailsRibDialog
import io.reactivex.functions.Consumer

internal class AppRootInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<Configuration>,
    private val dialogLauncher: DialogLauncher,
) : Interactor<AppRoot, AppRootView>(
    buildParams = buildParams
) {

    private lateinit var dialog: SightingDetailsRibDialog

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
        viewLifecycle.startStop {
            bind(dialog.rx2() to dialogEventConsumer)
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
            is AllSightingsList.Output.SightingDetailsRequested -> backStack.replace(
                Configuration.Overlay.SightingDetails(
                    it.id
                )
            )
        }
    }

    private val allSightingsMapOutputConsumer: Consumer<AllSightingsMap.Output> = Consumer {
        when (it) {
            is AllSightingsMap.Output.SightingDetailsRequested -> backStack.pushOverlay(
                Configuration.Overlay.SightingDetails(
                    it.id
                )
            )
        }
    }

    private val newSightingContainerOutputConsumer: Consumer<NewSightingContainer.Output> =
        Consumer {
            when (it) {
                is NewSightingContainer.Output.SightingAdded -> backStack.replace(
                    Configuration.Content.AllSightingsList
                )
            }
        }


    private val dialogEventConsumer: Consumer<SightingDetailsRibDialog.Event> = Consumer {
        when (it) {
            is SightingDetailsRibDialog.Event.NegativeButtonClicked -> dialogLauncher.hide(dialog)
            else -> {}
        }
    }


}
