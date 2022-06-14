package com.switcherette.boarribs.all_sightings_list

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.all_sightings_list.AllSightingsList.Input
import com.switcherette.boarribs.all_sightings_list.AllSightingsList.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class AllSightingsListNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<AllSightingsListView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<AllSightingsListView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), AllSightingsList, Connectable<Input, Output> by connector {

}
