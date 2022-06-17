package com.switcherette.boarribs.new_sighting_form.mapper


import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

internal object InputToWish : (NewSightingForm.Input) -> NewSightingFormFeature.Wish? {

    override fun invoke(input: NewSightingForm.Input): NewSightingFormFeature.Wish? =
        when(input){
            is NewSightingForm.Input.GrantPermissions -> NewSightingFormFeature.Wish.UpdatePermissions(input.permissions)
        }
}