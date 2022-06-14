package com.switcherette.boarribs.all_sightings_list

import com.switcherette.boarribs.all_sightings_list.feature.AllSightingsListFeature
import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class AllSightingsListInteractorTest {

    private val feature: AllSightingsListFeature = mock()
    private lateinit var interactor: AllSightingsListInteractor

    @Before
    fun setup() {
        interactor = AllSightingsListInteractor(
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
