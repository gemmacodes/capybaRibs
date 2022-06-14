package com.switcherette.boarribs.all_sightings_list.mapper

import com.switcherette.boarribs.all_sightings_list.AllSightingsList.Output
import com.switcherette.boarribs.all_sightings_list.AllSightingsListView.Event
import com.switcherette.boarribs.all_sightings_list.AllSightingsListView.Event.LoadSightingDetails

internal object ViewEventToOutput : (Event) -> Output? {

    override fun invoke(event: Event): Output? =
        when (event) {
            is LoadSightingDetails -> Output.SightingSelected(event.id)
        }
}