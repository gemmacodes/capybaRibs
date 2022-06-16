package com.switcherette.boarribs.new_sighting_form

import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.switcherette.boarribs.BuildConfig
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import com.switcherette.boarribs.new_sighting_form.mapper.NewsToOutput
import com.switcherette.boarribs.new_sighting_form.mapper.StateToViewModel
import com.switcherette.boarribs.new_sighting_form.mapper.ViewEventToWish
import com.switcherette.boarribs.new_sighting_form.mapper.InputToWish
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class NewSightingFormInteractor(
    buildParams: BuildParams<*>,
    private val feature: NewSightingFormFeature
) : Interactor<NewSightingForm, NewSightingFormView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
           bind(feature.news to rib.output using NewsToOutput)
            bind(rib.input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: NewSightingFormView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
           bind(feature to view using StateToViewModel)
           bind(view to feature using ViewEventToWish)
        }
    }

//    private var photoURI: Uri? = null
//    private val previewImage by lazy { binding.ivThumbnail }
//
//    private fun takeImage() {
//        getTmpFileUri().let { uri ->
//            photoURI = uri
//            takeImageResult.launch(uri)
//        }
//    }
//
//    private val takeImageResult =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
//            if (isSuccess) {
//                photoURI?.let { uri ->
//                    previewImage.setImageURI(uri)
//                    events.accept(NewSightingFormView.Event.UpdatePhotoURL(uri))
//                }
//            }
//        }
//
//    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
//
//    private val selectImageFromGalleryResult =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                previewImage.setImageURI(uri)
//                events.accept(NewSightingFormView.Event.UpdatePhotoURL(uri))
//            }
//        }
//
//    private fun getTmpFileUri(): Uri {
//
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
//        val tmpFile = File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */)
//        return FileProvider.getUriForFile(
//            context,
//            "${BuildConfig.APPLICATION_ID}.provider",
//            tmpFile
//        )
//    }

}
