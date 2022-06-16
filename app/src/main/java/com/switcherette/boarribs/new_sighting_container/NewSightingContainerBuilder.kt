@file:SuppressWarnings("LongParameterList")

package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerChildBuilders
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration

class NewSightingContainerBuilder(
    private val dependency: NewSightingContainer.Dependency,
) : SimpleBuilder<NewSightingContainer>() {

    override fun build(buildParams: BuildParams<Nothing?>): NewSightingContainer {
        val connections = NewSightingContainerChildBuilders(dependency)
        val customisation = buildParams.getOrDefault(NewSightingContainer.Customisation())
        val backStack = backStack(buildParams)
        val permissionRequester = dependency.permissionRequester
        val interactor = interactor(buildParams, backStack, permissionRequester)
        val router = router(buildParams, backStack, connections)

        return node(buildParams, customisation, interactor, router)
    }

    private fun backStack(buildParams: BuildParams<*>) =
        BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = Configuration.Content.NewSightingMap
        )

    private fun interactor(
        buildParams: BuildParams<*>,
        backStack: BackStack<Configuration>,
        permissionRequester: PermissionRequester,
    ) = NewSightingContainerInteractor(
        buildParams = buildParams,
        backStack = backStack,
        permissionRequester = permissionRequester
    )

    private fun router(
        buildParams: BuildParams<*>,
        routingSource: RoutingSource<Configuration>,
        builders: NewSightingContainerChildBuilders,
    ) = NewSightingContainerRouter(
        buildParams = buildParams,
        builders = builders,
        routingSource = routingSource,
    )

    private fun node(
        buildParams: BuildParams<*>,
        customisation: NewSightingContainer.Customisation,
        interactor: NewSightingContainerInteractor,
        router: NewSightingContainerRouter,
    ) = NewSightingContainerNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, router, disposables())
    )
}
