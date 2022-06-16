package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.routing.source.impl.Empty
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter
import com.badoo.ribs.test.emptyBuildParams
import org.mockito.kotlin.mock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class NewSightingContainerRouterTest {

    private var router: NewSightingContainerRouter? = null

    @Before
    fun setup() {
        router = NewSightingContainerRouter(
            buildParams = emptyBuildParams(),
            builders = mock(defaultAnswer = Answers.RETURNS_MOCKS),
            routingSource = Empty()
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
