package com.switcherette.boarribs.new_sighting_form

import android.view.View
import android.view.ViewConfiguration
import android.webkit.WebView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.PrecisionDescriber
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.util.*


class CustomGeneralClickAction @JvmOverloads constructor(
    private val tapper: Tapper, private val coordinatesProvider: CoordinatesProvider,
    private val precisionDescriber: PrecisionDescriber, rollbackAction: ViewAction? = null,
) :
    ViewAction {
    private val rollbackAction: Optional<ViewAction>
    override fun getConstraints(): Matcher<View> {
        val standardConstraint = isDisplayingAtLeast(80)
        return if (rollbackAction.isPresent) {
            Matchers.allOf(standardConstraint, rollbackAction.get().getConstraints())
        } else {
            standardConstraint
        }
    }

    override fun perform(uiController: UiController, view: View?) {
        val coordinates = coordinatesProvider.calculateCoordinates(view)
        val precision = precisionDescriber.describePrecision()
        var status = Tapper.Status.FAILURE
        var loopCount = 0

        while (status != Tapper.Status.SUCCESS && loopCount < 3) {
            status = try {
                tapper.sendTap(uiController, coordinates, precision)
            } catch (re: RuntimeException) {
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(re)
                    .build()
            }
            val duration = ViewConfiguration.getPressedStateDuration()
            // ensures that all work enqueued to process the tap has been run.
            if (duration > 0) {
                uiController.loopMainThreadForAtLeast(duration.toLong())
            }
            if (status == Tapper.Status.WARNING) {
                if (rollbackAction.isPresent) {
                    rollbackAction.get().perform(uiController, view)
                } else {
                    break
                }
            }
            loopCount++
        }
        if (status == Tapper.Status.FAILURE) {
            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(RuntimeException(String.format("Couldn't "
                        + "click at: %s,%s precision: %s, %s . Tapper: %s coordinate provider: %s precision " +
                        "describer: %s. Tried %s times. With Rollback? %s",
                    coordinates[0],
                    coordinates[1],
                    precision[0],
                    precision[1],
                    tapper,
                    coordinatesProvider,
                    precisionDescriber,
                    loopCount,
                    rollbackAction.isPresent)))
                .build()
        }
        if (tapper === Tap.SINGLE && view is WebView) {
            // WebViews will not process click events until double tap
            // timeout. Not the best place for this - but good for now.
            uiController.loopMainThreadForAtLeast(ViewConfiguration.getDoubleTapTimeout().toLong())
        }
    }

    override fun getDescription(): String {
        return tapper.toString().lowercase(Locale.getDefault()) + " click"
    }

    init {
        this.rollbackAction =
            Optional.ofNullable(rollbackAction)
    }
}