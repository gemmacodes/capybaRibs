package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Input
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Output
import io.reactivex.Single

interface NewSightingForm : Rib, Connectable<Input, Output> {

    interface Dependency : CanProvideActivityStarter{
        val sightingsDataSource: SightingsDataSource
    }

    data class BuildParams(
        val coordinates: Coordinates
    )

    sealed class Input

    sealed class Output{
        object SightingAdded : Output()
    }

    class Customisation(
        val viewFactory: NewSightingFormView.Factory = NewSightingFormViewImpl.Factory()
    ) : RibCustomisation


}
