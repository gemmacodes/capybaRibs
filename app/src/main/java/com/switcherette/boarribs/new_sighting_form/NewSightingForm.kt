package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Input
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Output
import com.switcherette.boarribs.utils.IdHelper
import com.switcherette.boarribs.utils.TimeHelper

interface NewSightingForm : Rib, Connectable<Input, Output> {

    interface Dependency {
        val sightingsDataSource: SightingsDataSource
        val timeHelper: TimeHelper
        val idHelper: IdHelper
        val defaultPictureUrl: String
    }

    data class BuildParams(
        val coordinates: Coordinates,
    )

    sealed class Input {
        data class StorePhoto(val filepath: String) : Input()
    }

    sealed class Output {
        object SightingAdded : Output()
        object CameraRequested : Output()
    }

    class Customisation(
        val viewFactory: NewSightingFormView.Factory = NewSightingFormViewImpl.Factory(),
    ) : RibCustomisation


}
