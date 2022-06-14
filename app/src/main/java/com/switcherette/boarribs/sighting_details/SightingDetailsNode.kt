package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.sighting_details.SightingDetails.Input
import com.switcherette.boarribs.sighting_details.SightingDetails.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class SightingDetailsNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<SightingDetailsView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<SightingDetailsView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), SightingDetails, Connectable<Input, Output> by connector {

}
