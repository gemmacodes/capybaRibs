package com.switcherette.boarribs.new_sighting_container

import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.modality.BuildContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.utils.IdHelper
import com.switcherette.boarribs.utils.TimeHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class NewSightingContainerWorkflowTest {

    private lateinit var workflow: NewSightingContainer

    @Before
    fun setup() {
        workflow = NewSightingContainerBuilder(object : NewSightingContainer.Dependency {
            override val sightingsDataSource: SightingsDataSource
                get() = mock()
            override val locationClient: FusedLocationProviderClient
                get() = mock()
            override val timeHelper: TimeHelper
                get() = mock()
            override val idHelper: IdHelper
                get() = mock()
            override val defaultPictureUrl: String
                get() = mock()
            override val permissionRequester: PermissionRequester
                get() = mock()
            override val activityStarter: ActivityStarter
                get() = mock()
        }).build(BuildContext.root(savedInstanceState = null)).also {
            it.node.onCreate()
        }
    }

    @After
    fun tearDown() {
    }

    /**
     * TODO: Add tests for every workflow action that operates on the node
     */
    @Test
    fun `business logic operation test`() {
        //workflow.businessLogicOperation()
        // verify(feature).accept(Wish)

        throw RuntimeException("Add real tests.")
    }

    /**
     * TODO: Add tests for every workflow action that attaches a child to ensure workflow step can be fulfilled
     */
    @Test
    fun `attach child workflow step is fulfillable`() {
        // val testObserver = TestObserver<Child>()

        // workflow.attachChild1().subscribe(testObserver)

        // testObserver.assertValueCount(1)
        // testObserver.assertComplete()

        throw RuntimeException("Add real tests.")
    }
}
