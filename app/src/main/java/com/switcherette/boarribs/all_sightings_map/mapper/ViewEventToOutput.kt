package com.switcherette.boarribs.all_sightings_map.mapper

import com.switcherette.boarribs.all_sightings_map.AllSightingsMap.Output
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.Event
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.Event.LoadSightingDetails

internal object ViewEventToOutput : (Event) -> Output? {

    override fun invoke(event: Event): Output? =
        when (event) {
            is LoadSightingDetails -> Output.SightingSelected(event.id)
        }
}