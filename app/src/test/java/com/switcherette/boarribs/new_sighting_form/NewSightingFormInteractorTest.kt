package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class NewSightingFormInteractorTest {

    private val feature: NewSightingFormFeature = mock()
    private lateinit var interactor: NewSightingFormInteractor

    @Before
    fun setup() {
        interactor = NewSightingFormInteractor(
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
