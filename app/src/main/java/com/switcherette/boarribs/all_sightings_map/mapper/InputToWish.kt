package com.switcherette.boarribs.all_sightings_map.mapper


import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature

internal object InputToWish : (AllSightingsMap.Input) -> AllSightingsMapFeature.Wish? {

    override fun invoke(input: AllSightingsMap.Input): AllSightingsMapFeature.Wish? =
        when (input) {
            is AllSightingsMap.Input.GrantPermissions -> AllSightingsMapFeature.Wish.UpdatePermissions(
                input.permissions)
        }
}