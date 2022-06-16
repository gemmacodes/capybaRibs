package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.badoo.ribs.core.customisation.RibCustomisation
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.app_root.AppRootView
import com.switcherette.boarribs.app_root.AppRootViewImpl
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer.Input
import com.switcherette.boarribs.new_sighting_container.NewSightingContainer.Output
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter
import io.reactivex.Single

interface NewSightingContainer : Rib, Connectable<Input, Output> {

    interface Dependency : CanProvidePermissionRequester, CanProvideActivityStarter {
        val sightingsDataSource: SightingsDataSource
        val locationClient: FusedLocationProviderClient
    }

    sealed class Input

    sealed class Output {
        object SightingAdded : Output()
    }

    class Customisation(
        val viewFactory: NewSightingContainerView.Factory = NewSightingContainerViewImpl.Factory()
    ) : RibCustomisation

}
