package com.switcherette.boarribs.new_sighting_map

import com.badoo.ribs.core.modality.BuildContext
import org.junit.After
import org.junit.Before
import org.junit.Test

class NewSightingMapWorkflowTest {

    private lateinit var workflow: NewSightingMap

    @Before
    fun setup() {
        workflow = NewSightingMapBuilder(object : NewSightingMap.Dependency {
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
