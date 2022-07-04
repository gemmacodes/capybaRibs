package com.switcherette.boarribs.app_root

import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.app_root.routing.AppRootRouter.Configuration
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class AppRootInteractorTest {

    private val dialogLauncher: DialogLauncher = mock()
    private val backStack: BackStack<Configuration> = mock()
    private lateinit var interactor: AppRootInteractor

    @Before
    fun setup() {
        interactor = AppRootInteractor(
            buildParams = emptyBuildParams(),
            backStack = backStack,
            dialogLauncher = dialogLauncher
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
