package com.switcherette.boarribs.all_sightings_map

import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature
import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class AllSightingsMapInteractorTest {

    private val feature: AllSightingsMapFeature = mock()
    private lateinit var interactor: AllSightingsMapInteractor

    @Before
    fun setup() {
        interactor = AllSightingsMapInteractor(
            buildParams = emptyBuildParams(),
            feature = feature
        )
    }

    @After
    fun tearDown() {
    }

    /**
     * TODO: Add real tests.
     */
    @Test
    fun `an example test with some conditions should pass`() {
        throw RuntimeException("Add real tests.")
    }
}
