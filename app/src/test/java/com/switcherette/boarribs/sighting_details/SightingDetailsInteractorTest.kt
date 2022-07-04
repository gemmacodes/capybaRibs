package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class SightingDetailsInteractorTest {

    private val feature: SightingDetailsFeature = mock()
    private val buildParams: BuildParams<SightingDetails.BuildParams> = mock()
    private lateinit var interactor: SightingDetailsInteractor

    @Before
    fun setup() {
        interactor = SightingDetailsInteractor(
            buildParams = buildParams,
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
