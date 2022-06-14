package com.switcherette.boarribs.new_sighting_map.mapper

import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.Event
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.Wish

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Wish? =
        when(event){
            is Event.SaveLocation -> Wish.SaveLocation(event.longitude, event.latitude)
            is Event.SetPointerLocation -> Wish.SetPointerLocation(event.longitude, event.latitude)
            else -> null
        }
}
