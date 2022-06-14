package com.switcherette.boarribs.nav_bar

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.binder.using
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.nav_bar.mapper.ViewEventToOutput

internal class NavBarInteractor(
    buildParams: BuildParams<*>
) : Interactor<NavBar, NavBarView>(
    buildParams = buildParams
) {

    override fun onViewCreated(view: NavBarView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to rib.output using ViewEventToOutput)
        }
    }
}
