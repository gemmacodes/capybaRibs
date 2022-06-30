package com.switcherette.boarribs.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.internal.ContextUtils.getActivity
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

        // Preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(surfaceProvider)
            }

        // Select back camera as a default
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture)

        } catch (exc: Exception) {
            Log.e("CameraXApp", "Use case binding failed", exc)
        }

    }, ContextCompat.getMainExecutor(context))
}


@SuppressLint("RestrictedApi")
fun takePhoto(context: Context, imageCapture: ImageCapture): Uri? {

    var photoUri: Uri? = null

    // Create time stamped name and MediaStore entry.
    val name = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }

    // Create output options object which contains file + metadata
    val outputOptions = getActivity(context)?.let {
        ImageCapture.OutputFileOptions
            .Builder(it.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()
    }

    // Set up image capture listener, which is triggered after photo has
    // been taken
    imageCapture.takePicture(
        outputOptions!!,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraXApp", "Photo capture failed: ${exc.message}", exc)
            }

            override fun
                    onImageSaved(output: ImageCapture.OutputFileResults) {
                val msg = "Photo capture succeeded: ${output.savedUri}"
                Log.d("CameraXApp", msg)
                photoUri = output.savedUri
            }
        }
    )

    return photoUri
}