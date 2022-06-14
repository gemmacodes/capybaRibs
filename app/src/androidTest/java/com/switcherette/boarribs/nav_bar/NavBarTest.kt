package com.switcherette.boarribs.nav_bar

import android.os.Bundle
import com.badoo.ribs.test.RibsRule
import com.badoo.ribs.test.RibTestActivity
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import org.junit.Rule
import org.junit.Test

class NavBarTest {

    @get:Rule
    val ribsRule = RibsRule { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

    // TODO use rib for interactions based on it implementing Connectable<Input, Output>
    lateinit var rib: NavBar

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        NavBarBuilder(
            object : NavBar.Dependency {}
        ).build(root(savedInstanceState)).also {
            rib = it
        }

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
    }
}
