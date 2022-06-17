package com.switcherette.boarribs.sighting_details

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.bumptech.glide.Glide
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.databinding.RibSightingDetailsBinding
import com.switcherette.boarribs.sighting_details.SightingDetailsView.ViewModel
import com.switcherette.boarribs.utils.bitmapFromDrawableRes
import io.reactivex.functions.Consumer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

interface SightingDetailsView : RibView,
    Consumer<ViewModel> {

    sealed class ViewModel {
        object Loading : ViewModel()
        data class Content(
            val sighting: Sighting,
        ) : ViewModel()
    }

    fun interface Factory : ViewFactory<SightingDetailsView>
}


class SightingDetailsViewImpl private constructor(
    override val androidView: ViewGroup
) : AndroidRibView(),
    SightingDetailsView,
    Consumer<ViewModel> {

    private val binding: RibSightingDetailsBinding = RibSightingDetailsBinding.bind(androidView)

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_sighting_details
    ) : SightingDetailsView.Factory {
        override fun invoke(context: ViewFactory.Context): SightingDetailsView =
            SightingDetailsViewImpl(
                context.inflate(layoutRes),
            )
    }

    override fun accept(vm: ViewModel) {
        when (vm) {
            is ViewModel.Content -> {
                with(binding) {
                    mapSighting.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(vm.sighting.coordinates.longitude, vm.sighting.coordinates.latitude))
                            .zoom(14.0)
                            .build()
                    )
                    mapSighting.getMapboxMap().loadStyleUri(
                        Style.MAPBOX_STREETS
                    ) {
                        addBoarAnnotation(vm.sighting.coordinates.longitude, vm.sighting.coordinates.latitude)
                    }

                    tvDate.text =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(vm.sighting.timestamp))
                    tvLocation.text =
                        "${((vm.sighting.coordinates.latitude * 100.0).roundToInt()) / 100.0}, ${((vm.sighting.coordinates.longitude * 100.0).roundToInt()) / 100.0}"
                    tvHeading.text = "${vm.sighting.heading}"
                    tvComments.text = vm.sighting.comments

                    chipAdults.text = "${vm.sighting.adults} adults"
                    chipPiglets.text = "${vm.sighting.piglets} piglets"
                    chipInteracting.text =
                        "${if (vm.sighting.interaction) "interacting" else "not interacting"}"

                    Glide
                        .with(view.context)
                        .load(vm.sighting.picture)
                        .into(binding.ivPhoto)
                }
            }
        }
    }

    private fun addBoarAnnotation(long: Double, lat: Double) {
        val annotationApi = binding.mapSighting.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(long, lat))
            .withIconImage(
                bitmapFromDrawableRes(
                    context,
                    R.drawable.capibara
                )!!
            )
            .withIconSize(0.2)
        pointAnnotationManager.create(pointAnnotationOptions)
    }
}
