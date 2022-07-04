package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class NewSightingContainerInteractorTest {

    private val permissionRequester: PermissionRequester = mock()
    private val backStack: BackStack<Configuration> = mock()
    private lateinit var interactor: NewSightingContainerInteractor

    @Before
    fun setup() {
        interactor = NewSightingContainerInteractor(
            buildParams = emptyBuildParams(),
            backStack = backStack,
            permissionRequester = permissionRequester
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
