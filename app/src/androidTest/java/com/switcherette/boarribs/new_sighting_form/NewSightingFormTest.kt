package com.switcherette.boarribs.new_sighting_form

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.test.RibsRule
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.data.SightingsDataSourceImpl
import com.switcherette.boarribs.utils.IdHelper
import com.switcherette.boarribs.utils.TimeHelper
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock


class NewSightingFormTest {

    @get:Rule
    val ribsRule =
        RibsRule(theme = R.style.Theme_BoarRibs) { activity, savedInstanceState ->
            buildRib(activity,
                savedInstanceState)
        }

    lateinit var rib: NewSightingForm

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        NewSightingFormBuilder(
            object : NewSightingForm.Dependency {
                override val sightingsDataSource: SightingsDataSource
                    get() = SightingsDataSourceImpl
                override val timeHelper: TimeHelper
                    get() = TimeHelper()
                override val idHelper: IdHelper
                    get() = IdHelper()
                override val defaultPictureUrl: String
                    get() = PICTURE
            }
        ).build(
            payload = NewSightingForm.BuildParams(Coordinates(LATITUDE, LONGITUDE)),
            buildContext = BuildContext.root(savedInstanceState = null)
        ).also {
            rib = it
        }

    @Test
    fun GIVEN_completed_form_WHEN_press_save_button_THEN_send_SightingAdded_Output() {
        val outputTest = rib.output.test()

        onView(withId(R.id.etHeading)).perform(replaceText(TITLE))
        onView(withId(R.id.et_comment)).perform(replaceText(COMMENT))
        onView(withId(R.id.et_numAdults)).perform(replaceText(ADULTS))
        onView(withId(R.id.et_numPups)).perform(replaceText(PUPS))
        onView(withId(R.id.btnS_environment)).perform(CustomGeneralClickAction(Tap.SINGLE,
            GeneralLocation.VISIBLE_CENTER,
            Press.FINGER))

        onView(withId(R.id.fab_addForm)).perform(click())

        outputTest.assertValue(NewSightingForm.Output.SightingAdded)
    }

    @Test
    fun GIVEN_not_completed_form_WHEN_press_save_button_THEN_send_SightingAdded_Output() {
        val outputTest = rib.output.test()

        onView(withId(R.id.fab_addForm)).perform(click())

        outputTest.assertNever(NewSightingForm.Output.SightingAdded)
    }

    @Test
    fun WHEN_click_on_addPicture_THEN_send_CameraRequested_Output() {
        val outputTest = rib.output.test()

        onView(withId(R.id.btn_add_picture)).perform(click())

        outputTest.assertValue(NewSightingForm.Output.CameraRequested)
    }

    companion object {
        private const val LATITUDE = 41.409428
        private const val LONGITUDE = 2.111255
        private const val TITLE = "title"
        private const val ADULTS = "2"
        private const val PUPS = "1"
        private const val COMMENT = "comment"
        private const val PICTURE = "PICTURE"
    }
}
