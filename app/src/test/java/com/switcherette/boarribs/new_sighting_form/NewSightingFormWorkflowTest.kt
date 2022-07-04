package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class NewSightingFormWorkflowTest {

    private lateinit var workflow: NewSightingForm

    @Before
    fun setup() {
        workflow = NewSightingFormBuilder(object : NewSightingForm.Dependency {
            override val sightingsDataSource: SightingsDataSource
                get() = TODO("Not yet implemented")
        }).build(buildParams = BuildParams(
            payload = NewSightingForm.BuildParams(mock()),
            buildContext = BuildContext.root(savedInstanceState = null)
        )).also {
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
