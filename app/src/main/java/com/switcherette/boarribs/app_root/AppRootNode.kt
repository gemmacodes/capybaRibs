package com.switcherette.boarribs.app_root

import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import com.switcherette.boarribs.app_root.AppRoot.Input
import com.switcherette.boarribs.app_root.AppRoot.Output
import io.reactivex.Single

class AppRootNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<AppRootView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<AppRootView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), AppRoot, Connectable<Input, Output> by connector {


}
