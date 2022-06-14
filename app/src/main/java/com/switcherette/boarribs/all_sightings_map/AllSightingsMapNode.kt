package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Input
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class AllSightingsMapNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<AllSightingsMapView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<AllSightingsMapView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), AllSightingsMap, Connectable<Input, Output> by connector {


}
