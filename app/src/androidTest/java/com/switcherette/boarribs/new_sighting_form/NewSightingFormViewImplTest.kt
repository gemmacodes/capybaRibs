package com.switcherette.boarribs.new_sighting_form

import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.badoo.ribs.test.view.RibsViewRule
import com.switcherette.boarribs.R
import io.reactivex.ObservableSource
import io.reactivex.observers.TestObserver
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference


class NewSightingFormViewImplTest {

    @get:Rule
    val rule = RibsViewRule(theme = R.style.Theme_BoarRibs) {
        NewSightingFormViewImpl.Factory().invoke(it)
    }

    @Test
    fun WHEN_picture_not_null_THEN_thumbnail_shows() {
        runOnMainSync {
            rule.view.accept(NewSightingFormView.ViewModel(picture = Uri.parse(PICTURE).toString()))
        }

        onView(withId(R.id.iv_thumbnail)).check(matches(isDisplayed()))
    }


    @Test
    fun WHEN_press_save_button_THEN_send_save_event() {
        runOnMainSync {
            rule.view.accept(NewSightingFormView.ViewModel(picture = Uri.parse(PICTURE).toString()))
        }

        val observer = rule.view.subscribeOnTestObserver()

        onView(withId(R.id.etHeading)).perform(replaceText(TITLE))
        onView(withId(R.id.et_comment)).perform(replaceText(COMMENT))
        onView(withId(R.id.et_numAdults)).perform(replaceText(ADULTS))
        onView(withId(R.id.et_numPups)).perform(replaceText(PUPS))
        onView(withId(R.id.btnS_environment)).perform(CustomGeneralClickAction(Tap.SINGLE,
            GeneralLocation.VISIBLE_CENTER,
            Press.FINGER))
        onView(withId(R.id.fab_addForm)).perform(click())

        observer.assertValue(NewSightingFormView.Event.SaveSighting(
            heading = TITLE,
            adults = ADULTS,
            pups = PUPS,
            comments = COMMENT,
            interaction = false
        ))
    }

    @Test
    fun WHEN_press_camera_button_THEN_send_CameraRequested_event() {
        runOnMainSync {
            rule.view.accept(NewSightingFormView.ViewModel(picture = Uri.parse(PICTURE).toString()))
        }

        val observer = rule.view.subscribeOnTestObserver()

        onView(withId(R.id.btn_add_picture)).perform(click())

        observer.assertValue(NewSightingFormView.Event.CameraRequested)
    }

//    @Test
//    fun WHEN_press_save_button_with_empty_fields_THEN_toast_shown() {
//        runOnMainSync {
//            rule.view.accept(NewSightingFormView.ViewModel(picture = Uri.parse(PICTURE).toString()))
//        }
//        onView(withId(R.id.fab_addForm)).perform(click())
//
//        onView(withText(R.string.mandatory_fields_toast))
//            .inRoot(withDecorView(not(rule.activity.window.decorView)))
//            .check(matches(isDisplayed()))
//
//    }


    private fun <T> ObservableSource<T>.subscribeOnTestObserver() = TestObserver<T>().apply {
        subscribe(this)
    }

    private fun <T> runOnMainSync(block: () -> T): T {
        val result = AtomicReference<T>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            result.set(block())
        }

        return result.get()
    }

    companion object {
        private const val TITLE = "title"
        private const val ADULTS = "2"
        private const val PUPS = "1"
        private const val COMMENT = "comment"
        private const val PICTURE =
            "android.resource://com.switcherette.boarribs/" + R.drawable.capybara
    }

}


