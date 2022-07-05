package com.switcherette.boarribs.new_sighting_container

import androidx.lifecycle.Lifecycle
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.replace
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.rx2.clienthelper.connector.NodeConnector
import com.badoo.ribs.test.assertEquals
import com.badoo.ribs.test.builder.RibBuilderStub
import com.badoo.ribs.test.emptyBuildParams
import com.badoo.ribs.test.node.RibNodeStub
import com.badoo.ribs.test.router.RibRouterTestHelper
import com.switcherette.boarribs.camera.Camera
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerChildBuilders
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class NewSightingContainerRouterTest() {

    private var router: NewSightingContainerRouter? = null

    private val backStack = BackStack<NewSightingContainerRouter.Configuration>(
        initialConfiguration = NewSightingContainerRouter.Configuration.Content.NewSightingMap,
        buildParams = emptyBuildParams(),
    )

    private val newSightingFormBuilder =
        RibBuilderStub<NewSightingForm.BuildParams, NewSightingForm> { params ->
            object : RibNodeStub<RibView>(params), NewSightingForm,
                Connectable<NewSightingForm.Input, NewSightingForm.Output> by NodeConnector() {}
        }

    private val newSightingMapBuilder = RibBuilderStub<Nothing?, NewSightingMap> { params ->
        object : RibNodeStub<RibView>(params), NewSightingMap,
            Connectable<NewSightingMap.Input, NewSightingMap.Output> by NodeConnector() {}
    }

    private val cameraBuilder = RibBuilderStub<Nothing?, Camera> { params ->
        object : RibNodeStub<RibView>(params), Camera,
            Connectable<Camera.Input, Camera.Output> by NodeConnector() {}
    }

    private val builders = mock<NewSightingContainerChildBuilders> {
        on { newSightingFormBuilder } doReturn newSightingFormBuilder
        on { newSightingMapBuilder } doReturn newSightingMapBuilder
        on { cameraBuilder } doReturn cameraBuilder
    }

    private val routerTestHelper = RibRouterTestHelper(
        buildParams = emptyBuildParams(),
        NewSightingContainerRouter(
            buildParams = emptyBuildParams(),
            builders = builders,
            routingSource = backStack,
        )
    )

    @After
    fun afterEach() {
        routerTestHelper.close()
    }

    @Before
    fun setup() {
        router = NewSightingContainerRouter(
            buildParams = emptyBuildParams(),
            builders = builders,
            routingSource = backStack
        )
    }

    @Test
    fun `GIVEN NewSightingContainer active WHEN NewSightingMap configuration is resolved THEN create NewSightingMap and attach it`() {
        backStack.replace(NewSightingContainerRouter.Configuration.Content.NewSightingMap)

        newSightingMapBuilder.assertLastNodeState(Lifecycle.State.CREATED)
    }

    @Test
    fun `GIVEN NewSightingContainer active WHEN Camera configuration is resolved THEN create Camera and attach it`() {
        backStack.replace(NewSightingContainerRouter.Configuration.Content.Camera)

        cameraBuilder.assertLastNodeState(Lifecycle.State.CREATED)
    }

    @Test
    fun `GIVEN NewSightingContainer active WHEN NewSightingForm configuration is resolved THEN create NewSightingForm with proper parameter and attach it`() {
        backStack.replace(NewSightingContainerRouter.Configuration.Content.NewSightingForm(
            Coordinates(LATITUDE, LONGITUDE)))

        newSightingFormBuilder.assertLastNodeState(Lifecycle.State.CREATED)
        newSightingFormBuilder.assertLastParam(NewSightingForm.BuildParams(Coordinates(LATITUDE, LONGITUDE)))
    }


    companion object {
        private const val LATITUDE = 41.409428
        private const val LONGITUDE = 2.111255
    }
}
