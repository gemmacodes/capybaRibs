package com.switcherette.boarribs.new_sighting_container

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.test.RibsRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class NewSightingContainerTest {

    @get:Rule
    val ribsRule = RibsRule(theme = R.style.Theme_BoarRibs) { activity, savedInstanceState ->
        buildRib(activity,
            savedInstanceState)
    }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: NewSightingContainer


    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        NewSightingContainerBuilder(
            object : NewSightingContainer.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = Mockito.mock(SightingsDataSource::class.java)
                override val locationClient: FusedLocationProviderClient
                    get() = Mockito.mock(FusedLocationProviderClient::class.java)
                override val permissionRequester: PermissionRequester
                    get() = Mockito.mock(PermissionRequester::class.java)
                override val activityStarter: ActivityStarter
                    get() = Mockito.mock(ActivityStarter::class.java)
            }
        ).build(root(savedInstanceState)).also {
            rib = it
        }

    @Test
    fun test() {
        TODO()
    }
}
