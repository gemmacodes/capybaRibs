package com.switcherette.boarribs.nav_bar

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.switcherette.boarribs.nav_bar.NavBar.Input
import com.switcherette.boarribs.nav_bar.NavBar.Output

interface NavBar : Rib, Connectable<Input, Output> {

    interface Dependency

    sealed class Input

    sealed class Output {
        object MapDisplayRequested : Output()
        object ListDisplayRequested : Output()
        object AddNewSightingRequested : Output()
    }

    class Customisation(
        val viewFactory: NavBarView.Factory = NavBarViewImpl.Factory(),
    ) : RibCustomisation

}
