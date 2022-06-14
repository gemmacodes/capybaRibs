package com.switcherette.boarribs.app_root

import com.badoo.ribs.routing.source.backstack.BackStack
import com.switcherette.boarribs.app_root.feature.AppRootFeature
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import com.badoo.ribs.test.emptyBuildParams
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppRootInteractorTest {

    private val feature: AppRootFeature = mock()
    private val backStack: BackStack<Configuration> = mock()
    private lateinit var interactor: AppRootInteractor

    @Before
    fun setup() {
        interactor = AppRootInteractor(
            buildParams = emptyBuildParams(),
            feature = feature,
            backStack = backStack
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
