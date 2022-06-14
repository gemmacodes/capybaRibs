package com.switcherette.boarribs.all_sightings_list

import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.switcherette.boarribs.all_sightings_list.AllSightingsList.Input
import com.switcherette.boarribs.all_sightings_list.AllSightingsList.Output
import com.switcherette.boarribs.data.SightingsDataSource
import io.reactivex.Single

interface AllSightingsList : Rib, Connectable<Input, Output> {

    interface Dependency: CanProvideDialogLauncher {
        val sightingsDataSource: SightingsDataSource
    }

    sealed class Input

    sealed class Output {
        data class SightingSelected(val id: String) : Output()
    }

    class Customisation(
        val viewFactory: AllSightingsListView.Factory = AllSightingsListViewImpl.Factory()
    ) : RibCustomisation

}
