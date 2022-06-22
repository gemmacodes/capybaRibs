package com.switcherette.boarribs.camera

import android.app.Activity
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.camera.CameraView.Event
import com.switcherette.boarribs.camera.CameraView.ViewModel
import com.switcherette.boarribs.databinding.RibCameraBinding
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import java.text.SimpleDateFormat
import java.util.*

interface CameraView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event{
        object PhotoCaptureRequested: Event()
    }

    data class ViewModel(
        val isCameraStarted: Boolean = true,
    )

    fun interface Factory : ViewFactory<CameraView>
}


class CameraViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : AndroidRibView(),
    CameraView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val binding = RibCameraBinding.bind(androidView)


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_camera,
    ) : CameraView.Factory {
        override fun invoke(context: ViewFactory.Context): CameraView =
            CameraViewImpl(
                context.inflate(layoutRes))
    }


    override fun accept(vm: ViewModel) {
        if (vm.isCameraStarted) {
            binding.btnImageCapture.setOnClickListener { events.accept(Event.PhotoCaptureRequested) }
        }
    }



}
