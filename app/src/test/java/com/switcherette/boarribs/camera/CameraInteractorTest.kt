package com.switcherette.boarribs.camera

import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.camera.feature.CameraFeature
import org.mockito.kotlin.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class CameraInteractorTest {

    private val feature: CameraFeature = mock()
    private lateinit var interactor: CameraInteractor

    @Before
    fun setup() {
        interactor = CameraInteractor(
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
