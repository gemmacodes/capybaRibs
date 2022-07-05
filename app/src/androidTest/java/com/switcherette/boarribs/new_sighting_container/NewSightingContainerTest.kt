package com.switcherette.boarribs.new_sighting_container

import android.os.Bundle
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.data.SightingsDataSource
import org.junit.Rule
import org.junit.Test

class NewSightingContainerTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: NewSightingContainer

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        NewSightingContainerBuilder(
            object : NewSightingContainer.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = TODO("Not yet implemented")
                override val locationClient: FusedLocationProviderClient
                    get() = TODO("Not yet implemented")
                override val permissionRequester: PermissionRequester
                    get() = TODO("Not yet implemented")
                override val activityStarter: ActivityStarter
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
