package com.switcherette.boarribs.nav_bar

import com.switcherette.boarribs.nav_bar.feature.NavBarFeature
import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class NavBarInteractorTest {

    private val feature: NavBarFeature = mock()
    private lateinit var interactor: NavBarInteractor

    @Before
    fun setup() {
        interactor = NavBarInteractor(
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
