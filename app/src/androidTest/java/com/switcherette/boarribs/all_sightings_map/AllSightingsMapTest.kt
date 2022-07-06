package com.switcherette.boarribs.all_sightings_map

import android.os.Bundle
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class AllSightingsMapTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: AllSightingsMap


    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        AllSightingsMapBuilder(
            object : AllSightingsMap.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = SightingsDataSourceImpl
            }
        ).build(root(savedInstanceState)).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }
}
