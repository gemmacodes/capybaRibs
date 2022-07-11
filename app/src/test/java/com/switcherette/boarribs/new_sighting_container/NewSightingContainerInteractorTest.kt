package com.switcherette.boarribs.new_sighting_container

import android.Manifest
import android.os.Build
import androidx.lifecycle.Lifecycle
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.test.emptyBuildParams
import com.badoo.ribs.test.integrationpoint.TestPermissionRequester
import com.badoo.ribs.test.interactor.RibInteractorTestHelper
import com.badoo.ribs.test.node.RibNodeStub
import com.badoo.ribs.test.view.RibViewStub
import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.camera.CameraView
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter.Configuration
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NewSightingContainerInteractorTest {

    private val permissionRequester: TestPermissionRequester = TestPermissionRequester()
    private val backStack: BackStack<Configuration> =
        BackStack(Configuration.Content.NewSightingMap, emptyBuildParams())
    private lateinit var interactor: NewSightingContainerInteractor
    private lateinit var interactorTestHelper: RibInteractorTestHelper<NewSightingContainer, NewSightingContainerView>
    private val view = object : RibViewStub<Nothing, Nothing>(), NewSightingContainerView {}

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
    fun `GIVEN permissions given WHEN Camera child sends PermissionsRequired output THEN GrantPermissions Input is sent`() {

        val camera =
            object : RibNodeStub<CameraView>(interactorTestHelper.createChildBuildParams()), Camera,
                Connectable<Camera.Input, Camera.Output> by NodeConnector() {}
        val cameraInput = camera.input.test()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {

            interactorTestHelper.attachChild(camera)

            permissionRequester.permissionsGiven()

            camera.output.accept(Camera.Output.PermissionsRequired(IMAGE_CAPTURE_PERMISSIONS))

            cameraInput.assertValue(Camera.Input.GrantPermissions(IMAGE_CAPTURE_PERMISSIONS))
        }
    }

    @Test
    fun `GIVEN permissions allowed WHEN Camera child sends PermissionsRequired output THEN GrantPermissions Input is sent`() {

        val camera =
            object : RibNodeStub<CameraView>(interactorTestHelper.createChildBuildParams()), Camera,
                Connectable<Camera.Input, Camera.Output> by NodeConnector() {}
        val cameraInput = camera.input.test()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {

            interactorTestHelper.attachChild(camera)

            permissionRequester.permissionsNotGiven()
            permissionRequester.allowAll()

            camera.output.accept(Camera.Output.PermissionsRequired(IMAGE_CAPTURE_PERMISSIONS))

            cameraInput.assertValue(Camera.Input.GrantPermissions(IMAGE_CAPTURE_PERMISSIONS))
        }
    }

    @Test
    fun `GIVEN permissions given WHEN NewSightingMap child sends PermissionsRequired output THEN GrantPermissions Input is sent`() {

        val map =
            object : RibNodeStub<NewSightingMapView>(interactorTestHelper.createChildBuildParams()), Camera,
                Connectable<Camera.Input, Camera.Output> by NodeConnector() {}
        val mapInput = map.input.test()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {

            interactorTestHelper.attachChild(map)

            permissionRequester.permissionsGiven()

            map.output.accept(Camera.Output.PermissionsRequired(LOCATION_PERMISSIONS))

            mapInput.assertValue(Camera.Input.GrantPermissions(LOCATION_PERMISSIONS))
        }
    }

    @Test
    fun `GIVEN permissions allowed WHEN NewSightingMap child sends PermissionsRequired output THEN GrantPermissions Input is sent`() {

        val map =
            object : RibNodeStub<NewSightingMapView>(interactorTestHelper.createChildBuildParams()), Camera,
                Connectable<Camera.Input, Camera.Output> by NodeConnector() {}
        val mapInput = map.input.test()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {

            interactorTestHelper.attachChild(map)

            permissionRequester.permissionsNotGiven()
            permissionRequester.allowAll()

            map.output.accept(Camera.Output.PermissionsRequired(LOCATION_PERMISSIONS))

            mapInput.assertValue(Camera.Input.GrantPermissions(LOCATION_PERMISSIONS))
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
        private val LOCATION_PERMISSIONS = listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
