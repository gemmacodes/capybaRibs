package com.switcherette.boarribs.new_sighting_container

import android.Manifest
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.test.rule.GrantPermissionRule
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.test.emptyBuildParams
import com.badoo.ribs.test.interactor.RibInteractorTestHelper
import com.badoo.ribs.test.node.RibNodeStub
import com.badoo.ribs.test.view.RibViewStub
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.camera.CameraView
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class NewSightingContainerInteractorTest {

    private val permissionRequester: PermissionRequester = mock()
    private val backStack: BackStack<Configuration> =
        BackStack(Configuration.Content.NewSightingMap, emptyBuildParams())
    private lateinit var interactor: NewSightingContainerInteractor
    private lateinit var interactorTestHelper: RibInteractorTestHelper<NewSightingContainer, NewSightingContainerView>
    private val view = object : RibViewStub<Nothing, Nothing>(), NewSightingContainerView {}

    @get:Rule
    val mCameraPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(NewSightingContainerInteractor)

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
            object : RibNodeStub<CameraView>(interactorTestHelper.createChildBuildParams()),
                Camera {
                override val input: Relay<Camera.Input> = PublishRelay.create()
                override val output: Relay<Camera.Output> = PublishRelay.create()
            }
        //Connectable<Camera.Input, Camera.Output> by NodeConnector() {}

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.CREATED) {

            // Attach Child to Interactor, Child will be in the same lifecycle state as the parent
            interactorTestHelper.attachChild(camera)

            // Fake Child output
            camera.output.accept(Camera.Output.PermissionsRequired(IMAGE_CAPTURE_PERMISSIONS))

            // Verify that navigate up was requested
            interactorTestHelper.integrationPoint.assertNavigatedUp()
        }
    }


    companion object {
        private val IMAGE_CAPTURE_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}
