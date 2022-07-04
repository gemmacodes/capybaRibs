package com.switcherette.boarribs.all_sightings_map

import com.badoo.ribs.core.modality.BuildContext
import com.switcherette.boarribs.data.SightingsDataSource
import org.junit.After
import org.junit.Before
import org.junit.Test

class AllSightingsMapWorkflowTest {

    private lateinit var workflow: AllSightingsMap

    @Before
    fun setup() {
        workflow = AllSightingsMapBuilder(object : AllSightingsMap.Dependency {
            override val sightingsDataSource: SightingsDataSource
                get() = TODO("Not yet implemented")
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
}
