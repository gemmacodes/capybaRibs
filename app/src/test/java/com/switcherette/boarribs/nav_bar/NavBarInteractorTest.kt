package com.switcherette.boarribs.nav_bar

import com.badoo.ribs.test.emptyBuildParams
import org.junit.After
import org.junit.Before
import org.junit.Test

class NavBarInteractorTest {


    private lateinit var interactor: NavBarInteractor

    @Before
    fun setup() {
        interactor = NavBarInteractor(
            buildParams = emptyBuildParams()
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
