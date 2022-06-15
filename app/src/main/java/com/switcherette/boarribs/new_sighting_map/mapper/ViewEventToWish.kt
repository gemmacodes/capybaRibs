package com.switcherette.boarribs.new_sighting_map.mapper

import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.Event
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.Wish

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Wish? =
        when(event){
            is Event.FindMyLocation -> Wish.FindMyLocation
            is Event.SaveLocation -> Wish.SaveLocation
            is Event.UpdateLocation -> Wish.UpdateBoarLocation(Coordinates( event.latitude, event.longitude))
        }
}
