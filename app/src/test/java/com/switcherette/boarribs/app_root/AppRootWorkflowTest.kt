package com.switcherette.boarribs.app_root

import com.badoo.ribs.core.modality.BuildContext
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppRootWorkflowTest {

    private lateinit var workflow: AppRoot

    @Before
    fun setup() {
        workflow = AppRootBuilder(object : AppRoot.Dependency {
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
        workflow.businessLogicOperation()
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
