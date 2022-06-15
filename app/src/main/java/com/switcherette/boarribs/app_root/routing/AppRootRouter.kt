package com.switcherette.boarribs.app_root.routing

import android.os.Parcelable
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.dialog.routing.resolution.DialogResolution.Companion.showDialog
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.switcherette.boarribs.sighting_details.dialog.SightingDetailsRibDialog
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration.*
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration.Permanent.NavBar
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import kotlinx.parcelize.Parcelize


class AppRootRouter internal constructor(
    buildParams: BuildParams<*>,
    routingSource: RoutingSource<Configuration>,
    private val builders: AppRootChildBuilders,
    private val dialogLauncher: DialogLauncher,
    transitionHandler: TransitionHandler<Configuration>? = null
): Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource + RoutingSource.permanent(NavBar),
    transitionHandler = transitionHandler
) {

    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration() {
            @Parcelize
            object NavBar: Configuration()
        }

        sealed class Content : Configuration() {
            @Parcelize
            object NewSightingMap : Configuration()
            @Parcelize
            data class NewSightingForm(val coordinates: Coordinates) : Configuration()
            @Parcelize
            object AllSightingsMap : Configuration()
            @Parcelize
            object AllSightingsList : Configuration()
        }

        sealed class Overlay : Configuration() {
            @Parcelize
            data class SightingDetails(val id: String): Overlay()
        }
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        with(builders) {
            when (val configuration = routing.configuration) {
                is Content.NewSightingMap -> child { newSightingMapBuilder.build(it) }
                is Content.NewSightingForm -> child { newSightingFormBuilder.build(it, NewSightingForm.BuildParams(configuration.coordinates))}
                is Content.AllSightingsMap -> child { allSightingsMapBuilder.build(it) }
                is Content.AllSightingsList -> child { allSightingsListBuilder.build(it) }
                is NavBar -> child { navBarBuilder.build(it) }
                is Overlay.SightingDetails -> showDialog(
                    routingSource,
                    routing.identifier,
                    dialogLauncher,
                    SightingDetailsRibDialog(sightingDetailsBuilder, configuration.id)
                )
            }
        }
}



