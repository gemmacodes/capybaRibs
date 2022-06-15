package com.switcherette.boarribs.app_root

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.clienthelper.childaware.whenChildBuilt
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.badoo.ribs.routing.source.backstack.operation.pushOverlay
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
    private val backStack: BackStack<Configuration>
) : Interactor<AppRoot, AppRootView>(
    buildParams = buildParams
) {

    private lateinit var dialogLauncher: DialogLauncher

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            dialogLauncher = node.integrationPoint.dialogLauncher
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
                bind(child.output to newSightingMapOutputConsumer)
            }
        }
        whenChildBuilt<NewSightingForm>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to newSightingFormOutputConsumer)
            }
        }
    }

    override fun onViewCreated(view: AppRootView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            //bind(dialog.rx2() to dialogEventConsumer)
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

    /*
    private val dialogEventConsumer: Consumer<SightingDetailsRibDialog.Event> = Consumer {
        when (it) {
            is SightingDetailsRibDialog.Event.NegativeButtonClicked -> dialogLauncher.hide(dialog)
            else -> {}
        }
    }
     */


    private val newSightingMapOutputConsumer: Consumer<NewSightingMap.Output> = Consumer {
        when (it) {
            is NewSightingMap.Output.LocationAdded -> backStack.push(Configuration.Content.NewSightingForm(Coordinates(it.longitude, it.latitude)))
        }
    }

    private val newSightingFormOutputConsumer: Consumer<NewSightingForm.Output> = Consumer {
        when (it) {
            NewSightingForm.Output.SightingAdded -> backStack.push(Configuration.Content.AllSightingsList)
        }
    }
}
