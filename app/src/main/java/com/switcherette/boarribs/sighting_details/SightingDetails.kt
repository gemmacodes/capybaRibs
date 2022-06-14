package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.sighting_details.SightingDetails.Input
import com.switcherette.boarribs.sighting_details.SightingDetails.Output
import io.reactivex.Single

interface SightingDetails : Rib, Connectable<Input, Output> {

    interface Dependency{
        val sightingsDataSource: SightingsDataSource
    }

    data class Params(
        val sightingId: String
    )

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: SightingDetailsView.Factory = SightingDetailsViewImpl.Factory()
    ) : RibCustomisation

}
