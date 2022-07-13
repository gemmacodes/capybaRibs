package com.switcherette.boarribs.app_root

import android.os.Bundle
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.utils.IdHelper
import com.switcherette.boarribs.utils.TimeHelper
import org.junit.Rule
import org.junit.Test

class AppRootTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: AppRoot

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        AppRootBuilder(
            object : AppRoot.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = TODO("Not yet implemented")
                override val locationClient: FusedLocationProviderClient
                    get() = TODO("Not yet implemented")
                override val timeHelper: TimeHelper
                    get() = TODO("Not yet implemented")
                override val idHelper: IdHelper
                    get() = TODO("Not yet implemented")
                override val defaultPictureUrl: String
                    get() = TODO("Not yet implemented")
                override val dialogLauncher: DialogLauncher
                    get() = TODO("Not yet implemented")
                override val activityStarter: ActivityStarter
                    get() = TODO("Not yet implemented")
                override val permissionRequester: PermissionRequester
                    get() = TODO("Not yet implemented")
            }
        ).build(root(savedInstanceState)).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }
}
