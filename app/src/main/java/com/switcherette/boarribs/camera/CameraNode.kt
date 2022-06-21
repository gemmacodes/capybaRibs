package com.switcherette.boarribs.camera

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.camera.Camera.Input
import com.switcherette.boarribs.camera.Camera.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class CameraNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<CameraView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<CameraView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), Camera, Connectable<Input, Output> by connector {

}
