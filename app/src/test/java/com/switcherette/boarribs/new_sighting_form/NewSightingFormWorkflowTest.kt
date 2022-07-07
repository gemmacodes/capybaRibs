package com.switcherette.boarribs.new_sighting_form

import com.badoo.ribs.core.modality.BuildContext
import com.switcherette.boarribs.data.SightingsDataSource
import org.junit.After
import org.junit.Before
import org.mockito.kotlin.mock

class NewSightingFormWorkflowTest {

    private lateinit var workflow: NewSightingForm

    @Before
    fun setup() {
        workflow = NewSightingFormBuilder(
            object : NewSightingForm.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = mock()
            }
        )
            .build(
                payload = NewSightingForm.BuildParams(mock()),
                buildContext = BuildContext.root(savedInstanceState = null)
            )
            .also {
                it.node.onCreate()
            }
    }

    @After
    fun tearDown() {
    }

}
