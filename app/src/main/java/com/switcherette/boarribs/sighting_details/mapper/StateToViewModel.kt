package com.switcherette.boarribs.sighting_details.mapper

import com.switcherette.boarribs.sighting_details.SightingDetailsView.ViewModel
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature.*
import com.switcherette.boarribs.sighting_details.feature.SightingDetailsFeature.State.*

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        when (state.content) {
            is Content.SightingLoaded -> ViewModel.Content(sighting = state.content.sighting!!)
            else -> ViewModel.Loading
        }
}
