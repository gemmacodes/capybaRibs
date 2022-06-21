package com.switcherette.boarribs.camera

import android.os.Bundle
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import org.junit.Rule
import org.junit.Test

class CameraTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: Camera

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        CameraBuilder(
            object : Camera.Dependency {}
        ).build(root(savedInstanceState)).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }
}
