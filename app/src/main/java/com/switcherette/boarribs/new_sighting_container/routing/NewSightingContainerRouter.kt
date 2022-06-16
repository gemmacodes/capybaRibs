package com.switcherette.boarribs.new_sighting_container.routing

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.switcherette.boarribs.app_root.routing.AppRootRouter
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import kotlinx.parcelize.Parcelize

class NewSightingContainerRouter internal constructor(
    buildParams: BuildParams<*>,
    routingSource: RoutingSource<Configuration>,
    private val builders: NewSightingContainerChildBuilders,
    transitionHandler: TransitionHandler<Configuration>? = null,
) : Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource,
    transitionHandler = transitionHandler
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration()

        sealed class Content : Configuration() {
            @Parcelize
            object NewSightingMap : Configuration()
            @Parcelize
            data class NewSightingForm(val coordinates: Coordinates) : Configuration()
        }

        sealed class Overlay : Configuration()
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        with(builders) {
            when (val configuration = routing.configuration) {
                is Configuration.Content.NewSightingMap -> ChildResolution.child {
                    newSightingMapBuilder.build(it)
                }
                is Configuration.Content.NewSightingForm -> ChildResolution.child {
                    newSightingFormBuilder.build(it,
                        NewSightingForm.BuildParams(configuration.coordinates))
                }
            }
        }
}

