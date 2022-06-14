package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Input
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Output
import com.switcherette.boarribs.data.SightingsDataSource

interface AllSightingsMap : Rib, Connectable<Input, Output> {

    interface Dependency: CanProvideDialogLauncher {
        val sightingsDataSource: SightingsDataSource
    }

    sealed class Input

    sealed class Output{
        data class SightingSelected(val id: String) : Output()
    }

    class Customisation(
        val viewFactory: AllSightingsMapView.Factory = AllSightingsMapViewImpl.Factory()
    ) : RibCustomisation

}
