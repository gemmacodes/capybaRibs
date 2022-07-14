package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Input
import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Output
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource

interface AllSightingsMap : Rib, Connectable<Input, Output> {

    interface Dependency : CanProvidePermissionRequester {
        val sightingsDataSource: SightingsDataSource
        val locationClient: FusedLocationProviderClient
    }

    sealed class Input {
        data class GrantPermissions(val permissions: List<String>) : Input()
    }

    sealed class Output {
        data class SightingDetailsRequested(val id: String) : Output()
        data class LocationAdded(val coordinates: Coordinates) : Output()
        data class PermissionsRequired(val permissions: List<String>) : Output()
    }

    class Customisation(
        val viewFactory: AllSightingsMapView.Factory = AllSightingsMapViewImpl.Factory(),
    ) : RibCustomisation

}
