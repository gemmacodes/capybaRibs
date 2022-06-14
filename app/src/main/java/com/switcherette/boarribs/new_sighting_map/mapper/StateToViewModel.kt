package com.switcherette.boarribs.new_sighting_map.mapper

import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.ViewModel
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.State

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        when(state.content){
            is State.Content.BoarCoordinates -> ViewModel.Content(state.content.longitude, state.content.latitude)
            State.Content.GeolocationError -> TODO()
        }
}
