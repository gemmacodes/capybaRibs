package com.switcherette.boarribs.new_sighting_map.mapper

import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.ViewModel
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.State

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        ViewModel(boarCoordinates = state.boarLocation)
}
