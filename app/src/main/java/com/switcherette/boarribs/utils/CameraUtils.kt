package com.switcherette.boarribs.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxrelay2.PublishRelay
import saveImage
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

//TODO: Should this logic be added somewhere else?
fun startCamera(
    context: Context,
    imageCapture: ImageCapture,
    surfaceProvider: Preview.SurfaceProvider,
    lifecycleOwner: LifecycleOwner,
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        // Used to bind the lifecycle of cameras to the lifecycle owner
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(surfaceProvider)
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture)

        } catch (exc: Exception) {
            Log.e("CameraX", "Use case binding failed", exc)
        }

    }, ContextCompat.getMainExecutor(context))
}


@SuppressLint("RestrictedApi")
fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    events: PublishRelay<CameraView.Event>,
) {

    // Set up image capture listener, which is triggered after photo has been taken
    imageCapture.takePicture(ContextCompat.getMainExecutor(context), object :
        ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            val bitmap = imageProxyToBitmap(image)
            val file = bitmap.let {
                saveImage(context,
                    it, SimpleDateFormat("yyyyMMdd_HHmm",
                        Locale.US).format(System.currentTimeMillis()))
            }
            val savedUri = Uri.fromFile(file)

            if (savedUri != null) {
                val msg = "Photo capture succeeded: $savedUri"
                Log.d("CameraX", msg)
                events.accept(CameraView.Event.PhotoCaptureRequested(savedUri))
            }

        }

        private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
            val planeProxy = image.planes[0]
            val buffer: ByteBuffer = planeProxy.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    })
}
