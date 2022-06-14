package com.switcherette.boarribs.nav_bar

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.switcherette.boarribs.nav_bar.NavBar.Input
import com.switcherette.boarribs.nav_bar.NavBar.Output
import io.reactivex.Single

interface NavBar : Rib, Connectable<Input, Output> {

    interface Dependency

    sealed class Input

    sealed class Output{
        object MapButtonClicked : Output()
        object ListButtonClicked : Output()
        object AddSightingButtonClicked : Output()
    }

    class Customisation(
        val viewFactory: NavBarView.Factory = NavBarViewImpl.Factory()
    ) : RibCustomisation

}
