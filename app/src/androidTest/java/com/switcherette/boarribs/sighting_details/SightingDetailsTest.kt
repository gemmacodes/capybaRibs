package com.switcherette.boarribs.sighting_details

import android.os.Bundle
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import org.junit.Rule
import org.junit.Test

class SightingDetailsTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: SightingDetails

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        SightingDetailsBuilder(
            object : SightingDetails.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = SightingsDataSourceImpl
            }
        ).build(
            payload = SightingDetails.BuildParams("id"),
            buildContext = BuildContext.root(savedInstanceState = null)
        ).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }
}
