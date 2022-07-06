package com.switcherette.boarribs.new_sighting_form


import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.badoo.ribs.test.view.RibsViewRule
import com.switcherette.boarribs.R
import io.reactivex.ObservableSource
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test


class NewSightingFormViewImplTest {

    @get:Rule
    val rule = RibsViewRule {
        NewSightingFormViewImpl.Factory().invoke(it)
    }

    @Test
    fun WHEN_press_save_button_THEN_send_save_event() {
        rule.view.accept(NewSightingFormView.ViewModel(picture = "picturepath.png"))

        val test = rule.view.androidView
        val observer = rule.view.subscribeOnTestObserver()

        onData(withId(R.id.etHeading)).perform(typeText(TITLE))
        onData(withId(R.id.et_comment)).perform(typeText(COMMENT))
        onData(withId(R.id.et_numAdults)).perform(typeText(ADULTS))
        onData(withId(R.id.et_numPups)).perform(typeText(PUPS))
        onView(withId(R.id.btnS_environment)).perform(click())
        onView(withId(R.id.btn_add_picture)).perform(click())

        observer.assertValue(NewSightingFormView.Event.SaveSighting(
            heading = TITLE,
            adults = ADULTS,
            pups = PUPS,
            comments = COMMENT,
            interaction = true
        ))
    }

    private fun <T> ObservableSource<T>.subscribeOnTestObserver() = TestObserver<T>().apply {
        subscribe(this)
    }

    companion object {
        private const val TITLE = "title"
        private const val ADULTS = "2"
        private const val PUPS = "1"
        private const val COMMENT = "comment"
    }

}
