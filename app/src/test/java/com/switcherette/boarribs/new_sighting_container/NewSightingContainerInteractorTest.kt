package com.switcherette.boarribs.new_sighting_container

import android.Manifest
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.test.emptyBuildParams
import com.badoo.ribs.test.interactor.RibInteractorTestHelper
import com.badoo.ribs.test.node.RibNodeStub
import com.badoo.ribs.test.view.RibViewStub
import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.camera.CameraView
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
class NewSightingContainerInteractorTest {

    private val permissionRequester: PermissionRequester = mock()
    private val backStack: BackStack<Configuration> =
        BackStack(Configuration.Content.NewSightingMap, emptyBuildParams())
    private lateinit var interactor: NewSightingContainerInteractor
    private lateinit var interactorTestHelper: RibInteractorTestHelper<NewSightingContainer, NewSightingContainerView>
    private val view = object : RibViewStub<Nothing, Nothing>(), NewSightingContainerView {}

    @get:Rule
    val mCameraPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Before
    fun setup() {
        interactor = NewSightingContainerInteractor(
            buildParams = emptyBuildParams(),
            backStack = backStack,
            permissionRequester = permissionRequester
        )

        // Utility helper that will allow you to invoke all required callbacks in order to setup the Interactor's state properly
        interactorTestHelper = RibInteractorTestHelper(
            interactor = interactor
        ) { NewSightingContainerNode(it, viewFactory = { view }, plugins = emptyList()) }

    }

    @Test
    fun `WHEN Camera child sends output THEN navigate up`() {
        // Create stub of Child1 with help of RibNodeStub
        val camera =
            object : RibNodeStub<CameraView>(interactorTestHelper.createChildBuildParams()), Camera,
                Connectable<Camera.Input, Camera.Output> by NodeConnector() {}


        `when`(permissionRequester.checkPermissions(
            interactorTestHelper.interactor,
            IMAGE_CAPTURE_PERMISSIONS.toTypedArray())).thenReturn(
            PermissionRequester.CheckPermissionsResult(
                granted = IMAGE_CAPTURE_PERMISSIONS,
                notGranted = emptyList(),
                shouldShowRationale = emptyList(),
            )
        )


        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {

            // Attach Child to Interactor, Child will be in the same lifecycle state as the parent
            interactorTestHelper.attachChild(camera)


            // Fake Child output
            camera.output.accept(Camera.Output.PermissionsRequired(IMAGE_CAPTURE_PERMISSIONS))


            // Verify that navigate up was requested
            interactorTestHelper.integrationPoint.assertNavigatedUp()
        }
    }

    private fun <T> runOnMainSync(block: () -> T): T {
        val result = AtomicReference<T>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            result.set(block())
        }

        return result.get()
    }

    companion object {
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        private const val REQUEST_GEOLOCATION = 1
        private const val REQUEST_IMAGE_ACCESS = 2
    }
}
