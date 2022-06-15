package com.switcherette.boarribs.app_root

import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.app_root.AppRoot.Input
import com.switcherette.boarribs.app_root.AppRoot.Output
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import io.reactivex.Single

interface AppRoot : Rib, Connectable<Input, Output> {

    interface Dependency : CanProvideDialogLauncher, CanProvidePermissionRequester, CanProvideActivityStarter {
        val sightingsDataSource: SightingsDataSource
        val locationClient: FusedLocationProviderClient
    }

    sealed class Input {
        object RequestPermissions : Input()
    }

    sealed class Output {
        data class PermissionsGranted(val permissions: List<String>) : Output()
    }

    class Customisation(
        val viewFactory: AppRootView.Factory = AppRootViewImpl.Factory()
    ) : RibCustomisation

    fun newSightingMap(): Single<NewSightingMap>

}

