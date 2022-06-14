package com.switcherette.boarribs.new_sighting_map

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.new_sighting_map.NewSightingMap.Input
import com.switcherette.boarribs.new_sighting_map.NewSightingMap.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class NewSightingMapNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<NewSightingMapView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<NewSightingMapView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), NewSightingMap, Connectable<Input, Output> by connector {

}
