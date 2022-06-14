package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Input
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Output
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import io.reactivex.Single

class NewSightingFormNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<NewSightingFormView>?,
    plugins: List<Plugin> = emptyList(),
    connector: NodeConnector<Input, Output> = NodeConnector()
) : RxWorkflowNode<NewSightingFormView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), NewSightingForm, Connectable<Input, Output> by connector {

}
