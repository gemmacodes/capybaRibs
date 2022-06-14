package com.switcherette.boarribs.new_sighting_map

import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature
import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class NewSightingMapInteractorTest {

    private val feature: NewSightingMapFeature = mock()
    private lateinit var interactor: NewSightingMapInteractor

    @Before
    fun setup() {
        interactor = NewSightingMapInteractor(
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
