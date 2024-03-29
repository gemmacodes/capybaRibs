package com.switcherette.boarribs.all_sightings_map

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.switcherette.boarribs.R
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.Event
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.ViewModel
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.utils.bitmapFromDrawableRes
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import pl.droidsonroids.gif.GifImageView

interface AllSightingsMapView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class LoadSightingDetails(val id: String) : Event()
    }

    sealed class ViewModel {
        object Loading : ViewModel()
        data class Content(
            val sightings: List<Sighting>,
            val coordinates: Coordinates
            ) : ViewModel()

    }

    fun interface Factory : ViewFactory<AllSightingsMapView>
}


class AllSightingsMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : AndroidRibView(),
    AllSightingsMapView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val sightingsMap: MapView by lazy { findViewById(R.id.mapView) }
    private val sightingsReported: TextView by lazy { findViewById(R.id.tvWelcome) }
    private val loadingAnimation: GifImageView by lazy { findViewById(R.id.ivLoading) }


    override fun accept(vm: ViewModel) {
        bind(vm)
    }

    fun bind(vm: ViewModel) {

        sightingsMap.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            when (vm) {
                is ViewModel.Content -> {
                    addBoarAnnotations(vm.sightings)
                    loadingAnimation.visibility = View.GONE
                    sightingsReported.text = vm.sightings.size.toString() + " " + context.getString(R.string.reported_capybaras)
                    sightingsMap.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .zoom(12.0)
                            .center(Point.fromLngLat(vm.coordinates.longitude, vm.coordinates.latitude))
                            .build()
                    )
                }
                is ViewModel.Loading -> {
                    loadingAnimation.visibility = View.VISIBLE
                    sightingsReported.text = context.getString(R.string.loading)
                }
            }
        }


    }


    private fun addBoarAnnotations(sightings: List<Sighting>?) {
        val annotationApi = sightingsMap.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        sightings?.map { sighting ->
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(sighting.coordinates.longitude,
                    sighting.coordinates.latitude))
                .withIconImage(
                    bitmapFromDrawableRes(
                        context,
                        R.drawable.capibara
                    )!!
                )
                .withIconSize(0.3)
            pointAnnotationManager.addClickListener {
                run { events.accept(Event.LoadSightingDetails(sighting.id)) }
                true
            }
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_all_sightings_map,
    ) : AllSightingsMapView.Factory {
        override fun invoke(context: ViewFactory.Context): AllSightingsMapView =
            AllSightingsMapViewImpl(
                context.inflate(layoutRes)
            )
    }

}
