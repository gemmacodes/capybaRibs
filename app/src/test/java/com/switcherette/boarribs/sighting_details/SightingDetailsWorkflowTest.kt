package com.switcherette.boarribs.sighting_details

import com.badoo.ribs.core.modality.BuildContext
import org.junit.After
import org.junit.Before
import org.junit.Test

class SightingDetailsWorkflowTest {

    private lateinit var workflow: SightingDetails

    @Before
    fun setup() {
        workflow = SightingDetailsBuilder(object : SightingDetails.Dependency {
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
}
