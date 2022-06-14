package com.switcherette.boarribs.new_sighting_form.feature

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature.*
import io.reactivex.Observable
import io.reactivex.Observable.empty
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal class NewSightingFormFeature(
    dataSource: SightingsDataSource,
    longitude: Double,
    latitude: Double
) : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = State(),
    bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(dataSource, longitude, latitude),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val id: String = "TBD",
        val heading: String = "TBD",
        val adults: Int = 0,
        val piglets: Int = 0,
        val interaction: Boolean = false,
        val comments: String = "TBD",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val timestamp: Long = 0,
        val picture: String = "TBD"
    )

    sealed class Wish {
        data class SaveSighting(
            val heading: String?,
            val adults: Int?,
            val piglets: Int?,
            val interaction: Boolean,
            val comments: String?,
            val picture: String?
        ) : Wish()

        object TakePhoto : Wish()
        object ChoosePhotoFromGallery : Wish()
    }

    sealed class Effect {
        object SightingSaved : Effect()
        data class PhotoTaken(val picturePath: String) : Effect()
        data class PhotoFromGalleryChosen(val picturePath: String) : Effect()
    }

    sealed class News {
        object SightingSaved : News()
    }

    class BootStrapperImpl : Bootstrapper<Wish> {
        override fun invoke(): Observable<Wish> =
            empty()
    }

    class ActorImpl(
        private val dataSource: SightingsDataSource,
        private val longitude: Double,
        private val latitude: Double
    ) : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when (wish) {
                is Wish.SaveSighting -> saveForm(dataSource, wish)
                Wish.ChoosePhotoFromGallery -> selectFromCamera()
                Wish.TakePhoto -> dispatchTakePictureIntent()
            }

        private fun saveForm(
            dataSource: SightingsDataSource,
            wish: Wish.SaveSighting
        ): Observable<Effect> {
            dataSource.saveSighting(
                Sighting(
                    id = UUID.randomUUID().toString(),
                    heading = wish.heading!!,
                    adults = wish.adults!!,
                    piglets = wish.piglets!!,
                    interaction = wish.interaction,
                    comments = wish.comments!!,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = System.currentTimeMillis(),
                    picture = wish.picture
                        ?: Uri.parse("android.resource://com.switcherette.boarribs/" + R.drawable.boar_img)
                            .toString()
                )
            )
            return Observable.just(Effect.SightingSaved)
        }


        private fun selectFromCamera(): Observable<Effect> {
            Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }.let {
                startActivityForResult(Intent.createChooser(it, "Select Picture"), 2)
                return Observable.just(Effect.PhotoFromGalleryChosen(it.data.toString()))
            }
        }

        private fun dispatchTakePictureIntent(): Observable<Effect> {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Toast.makeText(requireContext(), "Error, ouch!", Toast.LENGTH_SHORT).show()
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.switcherette.boarribs.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 1)
                    }
                    return if (photoFile != null) {
                        return Observable.just(Effect.PhotoTaken(photoFile.absolutePath))
                    } else Observable.empty()
                }

            }

        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                Effect.SightingSaved -> state
                is Effect.PhotoFromGalleryChosen -> state.copy(picture = effect.picturePath)
                is Effect.PhotoTaken -> state.copy(picture = effect.picturePath)
            }
    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when (effect) {
                Effect.SightingSaved -> News.SightingSaved
                is Effect.PhotoFromGalleryChosen -> TODO()
                is Effect.PhotoTaken -> TODO()
            }
    }
}
