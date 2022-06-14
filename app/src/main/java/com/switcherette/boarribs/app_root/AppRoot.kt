package com.switcherette.boarribs.app_root

import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.switcherette.boarribs.app_root.AppRoot.Input
import com.switcherette.boarribs.app_root.AppRoot.Output
import com.switcherette.boarribs.data.SightingsDataSource

interface AppRoot : Rib, Connectable<Input, Output> {

    interface Dependency : CanProvideDialogLauncher, CanProvidePermissionRequester {
        val sightingsDataSource: SightingsDataSource
    }

    sealed class Input {}

    sealed class Output {}

    class Customisation(
        val viewFactory: AppRootView.Factory = AppRootViewImpl.Factory()
    ) : RibCustomisation

}

