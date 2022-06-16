@file:SuppressWarnings("LongParameterList")

package com.switcherette.boarribs.app_root

import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.source.backstack.BackStack
import com.switcherette.boarribs.app_root.routing.AppRootChildBuilders
import com.switcherette.boarribs.app_root.routing.AppRootRouter
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration

class AppRootBuilder(
    private val dependency: AppRoot.Dependency
) : SimpleBuilder<AppRoot>() {

    override fun build(buildParams: BuildParams<Nothing?>): AppRoot {
        val connections = AppRootChildBuilders(dependency)
        val customisation = buildParams.getOrDefault(AppRoot.Customisation())
        val backStack = backStack(buildParams)
        val interactor = interactor(buildParams, backStack)
        val router = router(
            buildParams,
            backStack + RoutingSource.permanent(),
            connections,
            dependency.dialogLauncher
        )

        return node(buildParams, customisation, interactor, router)
    }

    private fun backStack(buildParams: BuildParams<*>) =
        BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = Configuration.Content.AllSightingsMap
        )

    private fun interactor(
        buildParams: BuildParams<*>,
        backStack: BackStack<Configuration>
    ) = AppRootInteractor(
        buildParams = buildParams,
        backStack = backStack
    )

    private fun router(
        buildParams: BuildParams<*>,
        routingSource: RoutingSource<Configuration>,
        builders: AppRootChildBuilders,
        dialogLauncher: DialogLauncher
    ) = AppRootRouter(
        buildParams = buildParams,
        builders = builders,
        routingSource = routingSource,
        dialogLauncher = dialogLauncher
    )

    private fun node(
        buildParams: BuildParams<*>,
        customisation: AppRoot.Customisation,
        interactor: AppRootInteractor,
        router: AppRootRouter
    ) = AppRootNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory,
        plugins = listOf(interactor, router)
    )
}
