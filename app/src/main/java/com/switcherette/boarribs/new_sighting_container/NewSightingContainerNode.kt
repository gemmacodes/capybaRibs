package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer.Input
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer.Output
import io.reactivex.Single

class NewSightingContainerNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<NewSightingContainerView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<NewSightingContainerView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), NewSightingContainer, Connectable<Input, Output> by connector {

}
