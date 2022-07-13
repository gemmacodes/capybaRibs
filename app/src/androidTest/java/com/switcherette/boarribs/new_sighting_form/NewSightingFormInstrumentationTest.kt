package com.switcherette.boarribs.new_sighting_form

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.ActivityTestRule
import com.switcherette.boarribs.MainActivity
import com.switcherette.boarribs.R
import org.junit.Rule
import org.junit.Test


class NewSightingFormInstrumentationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun GIVEN_complete_form_WHEN_press_save_button_THEN_send_SightingAdded_Output() {
        onView(withId(R.id.addButton)).perform(click())
        onView(withId(R.id.fab_addMap)).perform(click())

        onView(withId(R.id.etHeading)).perform(replaceText(TITLE))
        onView(withId(R.id.et_comment)).perform(replaceText(COMMENT))
        onView(withId(R.id.et_numAdults)).perform(replaceText(ADULTS))
        onView(withId(R.id.et_numPups)).perform(replaceText(PUPS))
        onView(withId(R.id.btnS_environment)).perform(CustomGeneralClickAction(Tap.SINGLE,
            GeneralLocation.VISIBLE_CENTER,
            Press.FINGER))

        onView(withId(R.id.fab_addForm)).perform(click())

        onView(withId(R.id.rv_show_list))
            .check(matches(isDisplayed()))
    }

    @Test
    fun GIVEN_uncomplete_form_WHEN_press_save_button_THEN_send_SightingAdded_Output() {
        onView(withId(R.id.addButton)).perform(click())
        onView(withId(R.id.fab_addMap)).perform(click())
        onView(withId(R.id.fab_addForm)).perform(click())

        onView(withId(R.id.rv_show_list))
            .check(doesNotExist())
    }


    companion object {
        private const val TITLE = "title"
        private const val ADULTS = "2"
        private const val PUPS = "1"
        private const val COMMENT = "comment"
    }
}
