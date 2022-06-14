package com.switcherette.boarribs.all_sightings_map

import android.view.ViewGroup
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
import com.switcherette.boarribs.all_sightings_list.AllSightingsListView
import com.switcherette.boarribs.all_sightings_list.AllSightingsListViewImpl
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.Event
import com.switcherette.boarribs.all_sightings_map.AllSightingsMapView.ViewModel
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.utils.bitmapFromDrawableRes
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface AllSightingsMapView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class LoadSightingDetails(val id: String) : Event()
    }

    sealed class ViewModel {
        object Loading : ViewModel()
        data class Content(val sightings: List<Sighting>) : ViewModel()
    }

    fun interface Factory : ViewFactory<AllSightingsMapView>
}


class AllSightingsMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    AllSightingsMapView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val sightingsMap: MapView by lazy { findViewById(R.id.mapView) }


    override fun accept(vm: ViewModel) {
        bind(vm)
    }

    fun bind(vm: ViewModel) {
        sightingsMap.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(11.0)
                .build()
        )
        sightingsMap.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            when (vm) {
                is ViewModel.Content -> addBoarAnnotations(vm.sightings)
                else -> {}
            }
        }
    }


    private fun addBoarAnnotations(sightings: List<Sighting>?) {
        val annotationApi = sightingsMap.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        sightings?.map { sighting ->
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(sighting.longitude, sighting.latitude))
                .withIconImage(
                    bitmapFromDrawableRes(
                        context,
                        R.drawable.boar
                    )!!
                )
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

//    fun addClickListener(u: OnPointAnnotationClickListener): Boolean {
//        event.accept(Event.ShowSightingDetails(u.getId()))
//    }

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_all_sightings_map
    ) : AllSightingsMapView.Factory {
        override fun invoke(context: ViewFactory.Context): AllSightingsMapView =
            AllSightingsMapViewImpl(
                context.inflate(layoutRes)
            )
    }

}
