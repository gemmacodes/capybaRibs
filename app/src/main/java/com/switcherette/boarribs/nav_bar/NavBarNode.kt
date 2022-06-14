package com.switcherette.boarribs.nav_bar

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.nav_bar.NavBar.Input
import com.switcherette.boarribs.nav_bar.NavBar.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class NavBarNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<NavBarView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<NavBarView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), NavBar, Connectable<Input, Output> by connector {


}
