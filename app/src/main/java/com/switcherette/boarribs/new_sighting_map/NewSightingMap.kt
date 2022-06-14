package com.switcherette.boarribs.new_sighting_map

import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_map.NewSightingMap.Input
import com.switcherette.boarribs.new_sighting_map.NewSightingMap.Output
import io.reactivex.Single

interface NewSightingMap : Rib, Connectable<Input, Output> {

    interface Dependency: CanProvidePermissionRequester {
    }

    sealed class Input {
    }

    sealed class Output{
        data class LocationAdded(val longitude: Double, val latitude: Double) : Output()
    }

    class Customisation(
        val viewFactory: NewSightingMapView.Factory = NewSightingMapViewImpl.Factory()
    ) : RibCustomisation


}
