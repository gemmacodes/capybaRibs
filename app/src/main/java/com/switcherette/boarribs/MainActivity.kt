package com.switcherette.boarribs

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.dialog.AlertDialogLauncher
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.switcherette.boarribs.app_root.AppRoot
import com.switcherette.boarribs.app_root.AppRootBuilder
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl


class MainActivity : RibActivity() {

    private val root: FrameLayout by lazy { findViewById(R.id.root) }

    override val rootViewGroup: ViewGroup
        get() = root

    val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this) }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
    }

    override fun createRib(savedInstanceState: Bundle?): Rib {
        return AppRootBuilder(
            dependency = object : AppRoot.Dependency {
                override val sightingsDataSource: SightingsDataSource = SightingsDataSourceImpl

                override val permissionRequester: PermissionRequester
                    get() = integrationPoint.permissionRequester

                override val dialogLauncher: DialogLauncher
                    get() = integrationPoint.dialogLauncher

                override val activityStarter: ActivityStarter
                    get() = integrationPoint.activityStarter

                override val locationClient: FusedLocationProviderClient
                    get() = fusedLocationClient
            }
        )
            .build(BuildContext.root(savedInstanceState = savedInstanceState))
    }

}

