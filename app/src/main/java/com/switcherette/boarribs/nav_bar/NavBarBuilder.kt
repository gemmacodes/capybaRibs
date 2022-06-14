package com.switcherette.boarribs.nav_bar

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams

class NavBarBuilder(
    private val dependency: NavBar.Dependency
) : SimpleBuilder<NavBar>() {

    override fun build(buildParams: BuildParams<Nothing?>): NavBar {
        val customisation = buildParams.getOrDefault(NavBar.Customisation())
        val interactor = interactor(buildParams)
        return node(buildParams, customisation, interactor)
    }

    private fun interactor(
        buildParams: BuildParams<*>,
    ) = NavBarInteractor(
            buildParams = buildParams,
        )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: NavBar.Customisation,
        interactor: NavBarInteractor
    ) = NavBarNode(
            buildParams = buildParams,
            viewFactory = customisation.viewFactory,
            plugins = listOf(interactor)
        )
}
