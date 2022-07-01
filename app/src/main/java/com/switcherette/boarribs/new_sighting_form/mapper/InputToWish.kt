package com.switcherette.boarribs.new_sighting_form.mapper


import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature.*

internal object InputToWish : (NewSightingForm.Input) -> Wish? {

    override fun invoke(input: NewSightingForm.Input): Wish? =
        when(input){
            is NewSightingForm.Input.StorePhoto -> Wish.UpdatePhotoUri(input.filepath)
        }
}