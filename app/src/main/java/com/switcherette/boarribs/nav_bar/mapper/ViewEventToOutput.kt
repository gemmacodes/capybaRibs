package com.switcherette.boarribs.nav_bar.mapper

import com.switcherette.boarribs.nav_bar.NavBar
import com.switcherette.boarribs.nav_bar.NavBarView.Event

internal object ViewEventToOutput : (Event) -> NavBar.Output? {

    override fun invoke(event: Event): NavBar.Output? = when (event){
        Event.MapButtonClicked -> NavBar.Output.MapDisplayRequested
        Event.ListButtonClicked -> NavBar.Output.ListDisplayRequested
        Event.AddNewSightingButtonClicked -> NavBar.Output.AddNewSightingRequested
    }

}
