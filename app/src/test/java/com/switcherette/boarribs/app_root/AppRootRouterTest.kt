package com.switcherette.boarribs.app_root

import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.routing.source.impl.Empty
import com.badoo.ribs.test.emptyBuildParams
import com.switcherette.boarribs.app_root.routing.AppRootRouter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AppRootRouterTest {

    private var router: AppRootRouter? = null
    private var dialogLauncher: DialogLauncher = mock()

    @Before
    fun setup() {
        router = AppRootRouter(
            buildParams = emptyBuildParams(),
            builders = mock(defaultAnswer = Answers.RETURNS_MOCKS),
            routingSource = Empty(),
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
