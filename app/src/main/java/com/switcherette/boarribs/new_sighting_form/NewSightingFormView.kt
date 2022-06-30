package com.switcherette.boarribs.new_sighting_form

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat.startActivityForResult
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

interface NewSightingFormView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class SaveSighting(
            val heading: String?,
            val adults: String?,
            val pups: String?,
            val interaction: Boolean,
            val comments: String?,
        ) : Event()

        object CameraRequested : Event()
    }

    data class ViewModel(
        val picture: String?
    )

    fun interface Factory : ViewFactory<NewSightingFormView>
}


class NewSightingFormViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : AndroidRibView(),
    NewSightingFormView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_form,
    ) : NewSightingFormView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingFormView =
            NewSightingFormViewImpl(
                context.inflate(layoutRes)
            )
    }

    private val binding: RibNewSightingFormBinding = RibNewSightingFormBinding.bind(androidView)


    override fun accept(vm: ViewModel) {
        with(binding) {
            Glide
                .with(context)
                .load(vm.picture)
                .into(binding.ivThumbnail)

            fabAddForm.setOnClickListener {
                val heading = etHeading.text.toString().trim()
                val adults = etNumAdults.text.toString().trim()
                val pups = etNumPups.text.toString().trim()
                val interaction = btnSEnvironment.isChecked
                val comments = etComment.text.toString().trim()

                events.accept(
                    Event.SaveSighting(
                        heading,
                        adults,
                        pups,
                        interaction,
                        comments
                    )
                )

            }

            btnAddPicture.setOnClickListener {
                events.accept(Event.CameraRequested)
                //showConfirmationDialog(it)
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
                events.accept(Event.CameraRequested)
            } else if (options[item] == context.getString(R.string.choose_gallery)) {
                //TODO
            } else if (options[item] == context.getString(R.string.cancel_photo)) {
                dialog.dismiss()
            }
        })
        builder.show()
    }


}
