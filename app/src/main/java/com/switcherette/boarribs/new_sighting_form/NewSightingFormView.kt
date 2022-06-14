package com.switcherette.boarribs.new_sighting_form

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.FileProvider
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.databinding.RibNewSightingFormBinding
import com.switcherette.boarribs.new_sighting_form.NewSightingFormView.Event
import com.switcherette.boarribs.new_sighting_form.NewSightingFormView.ViewModel
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import java.io.File
import java.io.IOException

interface NewSightingFormView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class SaveSighting(
            val heading: String?,
            val adults: Int?,
            val piglets: Int?,
            val interaction: Boolean,
            val comments: String?,
            val picture: String?
        ) : Event()

        object TakePhoto: Event()
        object ChoosePhotoFromGallery: Event()

    }

    data class ViewModel(
        val heading: String?,
        val adults: Int?,
        val piglets: Int?,
        val interaction: Boolean,
        val comments: String?,
        val picture: String?
    )

    fun interface Factory : ViewFactory<NewSightingFormView>
}


class NewSightingFormViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NewSightingFormView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_form
    ) : NewSightingFormView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingFormView =
            NewSightingFormViewImpl(
                context.inflate(layoutRes)
            )
    }

    private val binding: RibNewSightingFormBinding = RibNewSightingFormBinding.bind(androidView)

    override fun accept(vm: ViewModel) {
        with(binding) {

            val heading = etHeading.text.toString().trim()
            val adults = etNumAdults.text.toString()
            val piglets = etNumPiglets.text.toString()
            val interaction = btnSEnvironment.isChecked
            var comments = etComment.text.toString().trim()
            if (comments.isEmpty()) {
                comments = context.getString(R.string.no_comments)
            }
            val picturePath = null

            fabAddForm.setOnClickListener {
                if (heading.isNotEmpty() && adults.isNotEmpty() && piglets.isNotEmpty()) {
                    events.accept(
                        Event.SaveSighting(
                            heading,
                            adults.toInt(),
                            piglets.toInt(),
                            interaction,
                            comments,
                            picturePath
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "'Title', 'Adults' and 'Piglets' are mandatory fields!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnAddPicture.setOnClickListener {
                showConfirmationDialog(it)
            }


        }
    }

    private fun showConfirmationDialog(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(context.getString(R.string.add_photo))

        val options = arrayOf<CharSequence>(
            context.getString(R.string.take_photo),
            context.getString(R.string.choose_gallery),
            context.getString(R.string.cancel_photo)
        )
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == context.getString(R.string.take_photo)) {
                events.accept(Event.TakePhoto)
            } else if (options[item] == context.getString(R.string.choose_gallery)) {
                events.accept(Event.ChoosePhotoFromGallery)
            } else if (options[item] == context.getString(R.string.cancel_photo)) {
                dialog.dismiss()
            }
        })
        builder.show()
    }

}
