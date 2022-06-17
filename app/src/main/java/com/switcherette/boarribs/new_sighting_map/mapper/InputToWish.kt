package com.switcherette.boarribs.new_sighting_map.mapper


import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature

internal object InputToWish : (NewSightingMap.Input) -> NewSightingMapFeature.Wish? {

    override fun invoke(input: NewSightingMap.Input): NewSightingMapFeature.Wish? =
        when(input){
            is NewSightingMap.Input.GrantPermissions -> NewSightingMapFeature.Wish.UpdatePermissions(input.permissions)
        }
}