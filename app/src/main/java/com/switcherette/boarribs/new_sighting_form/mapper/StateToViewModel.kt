package com.switcherette.boarribs.new_sighting_form.mapper

import com.switcherette.boarribs.new_sighting_form.NewSightingFormView.ViewModel
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView

internal object StateToViewModel : (NewSightingFormFeature.State) -> ViewModel {

    override fun invoke(state: NewSightingFormFeature.State): ViewModel {
        return ViewModel(
            picture = state.picture,
        )
    }
}
