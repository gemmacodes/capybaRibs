package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class SightingDetailsInteractorTest {

    private val feature: SightingDetailsFeature = mock()
    private lateinit var interactor: SightingDetailsInteractor

    @Before
    fun setup() {
        interactor = SightingDetailsInteractor(
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
