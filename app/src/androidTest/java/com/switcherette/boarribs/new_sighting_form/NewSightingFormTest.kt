package com.switcherette.boarribs.new_sighting_form

import android.os.Bundle
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.bumptech.glide.load.DataSource
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock


class NewSightingFormTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: NewSightingForm

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        NewSightingFormBuilder(
            object : NewSightingForm.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = SightingsDataSourceImpl
            }
        ).build(
            payload = NewSightingForm.BuildParams(Coordinates(LATITUDE, LONGITUDE)),
            buildContext = BuildContext.root(savedInstanceState = null)
        ).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }

    companion object {
        private const val LATITUDE = 41.409428
        private const val LONGITUDE = 2.111255
    }
}
