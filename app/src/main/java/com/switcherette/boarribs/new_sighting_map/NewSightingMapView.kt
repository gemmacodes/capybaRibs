package com.switcherette.boarribs.new_sighting_map

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.Annotation
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.databinding.RibNewSightingMapBinding
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.Event
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.ViewModel
import com.switcherette.boarribs.utils.bitmapFromDrawableRes
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer


interface NewSightingMapView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object FindMyLocation : Event()
        data class UpdateLocation(val longitude: Double, val latitude: Double) : Event()
        object SaveLocation : Event()
    }

    data class ViewModel(
        val boarCoordinates: Coordinates,
    )


    fun interface Factory : ViewFactory<NewSightingMapView>
}


class NewSightingMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : AndroidRibView(),
    NewSightingMapView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val binding: RibNewSightingMapBinding = RibNewSightingMapBinding.bind(androidView)

    private var boarAnnotation: PointAnnotation? = null
    private var pointAnnotationManager: PointAnnotationManager

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_map,
    ) : NewSightingMapView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingMapView =
            NewSightingMapViewImpl(
                context.inflate(layoutRes),
            )
    }

    init {
        with(binding) {
            fabAddMap.setOnClickListener {
                events.accept(Event.SaveLocation)
            }
            btnGeoLoc.setOnClickListener {
                events.accept(Event.FindMyLocation)
            }
            mapAdd.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS
            ) {
                initLocationComponent()
                mapAdd.gestures.addOnMoveListener(onMoveListener)
                binding.ivLoading.visibility = View.GONE
            }
            pointAnnotationManager = binding.mapAdd.annotations.createPointAnnotationManager()
        }
    }

    override fun accept(vm: ViewModel) {
        with(binding) {
            updateBoarLocation(vm.boarCoordinates)
            mapAdd.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .zoom(14.0)
                    .center(Point.fromLngLat(vm.boarCoordinates.longitude,
                        vm.boarCoordinates.latitude))
                    .build()
            )
        }
    }

    private fun updateBoarLocation(coordinates: Coordinates) {
        val boarAnnotation = boarAnnotation ?: createBoarAnnotation(coordinates)
        boarAnnotation.point = Point.fromLngLat(coordinates.longitude, coordinates.latitude)
        pointAnnotationManager.update(boarAnnotation)
    }

    private fun createBoarAnnotation(coordinates: Coordinates): PointAnnotation =
        bitmapFromDrawableRes(context, R.drawable.capibara)!!.let {
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(coordinates.longitude, coordinates.latitude))
                .withIconImage(it)
                .withIconSize(0.3)
                .withDraggable(true)

            pointAnnotationManager.addDragListener(dragListener)
            pointAnnotationManager.create(pointAnnotationOptions)
        }.also {
            boarAnnotation = it
        }

    private val dragListener = object : OnPointAnnotationDragListener {
        override fun onAnnotationDrag(annotation: Annotation<*>) {}
        override fun onAnnotationDragStarted(annotation: Annotation<*>) {}
        override fun onAnnotationDragFinished(annotation: Annotation<*>) {
            events.accept(
                Event.UpdateLocation(
                    (annotation as PointAnnotation).point.longitude(),
                    annotation.point.latitude()
                )
            )
        }
    }


    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapAdd.location

        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = false
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    context,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    context,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onCameraTrackingDismissed() {
        binding.mapAdd.gestures.removeOnMoveListener(onMoveListener)
    }

}




