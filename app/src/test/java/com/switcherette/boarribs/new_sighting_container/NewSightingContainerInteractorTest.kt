package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.routing.source.backstack.BackStack
import com.switcherette.boarribs.new_sighting_container.feature.NewSightingContainerFeature
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import com.badoo.ribs.test.emptyBuildParams
import org.mockito.kotlin.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class NewSightingContainerInteractorTest {

    private val feature: NewSightingContainerFeature = mock()
    private val backStack: BackStack<Configuration> = mock()
    private lateinit var interactor: NewSightingContainerInteractor

    @Before
    fun setup() {
        interactor = NewSightingContainerInteractor(
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
