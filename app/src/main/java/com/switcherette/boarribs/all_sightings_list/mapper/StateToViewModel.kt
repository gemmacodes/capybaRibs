package com.switcherette.boarribs.all_sightings_list.mapper

import com.switcherette.boarribs.all_sightings_list.AllSightingsListView.ViewModel
import com.switcherette.boarribs.all_sightings_list.feature.AllSightingsListFeature.State

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        when(state.content){
            is State.Content.SightingsLoaded -> ViewModel.Content(
                sightings = state.content.sightings
            )
            else -> ViewModel.Loading
        }
}


