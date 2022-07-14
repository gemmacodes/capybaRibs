package com.switcherette.boarribs.all_sightings_map.mapper

import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.ViewModel
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature.State

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        when(state.content){
            is State.Content.SightingsLoaded -> ViewModel.Content(
                sightings = state.content.sightings,
                coordinates = state.userLocation
            )
            else -> ViewModel.Loading
        }
}


