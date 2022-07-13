package com.switcherette.boarribs.new_sighting_form

import com.switcherette.boarribs.data.Coordinates
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.SightingsDataSource
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature
import com.switcherette.boarribs.utils.IdHelper
import com.switcherette.boarribs.utils.TimeHelper
import io.reactivex.Observable
import io.reactivex.ObservableSource
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


class NewSightingFormFeatureTest {

    private val dataSource = mock<SightingsDataSource>()
    private val mTime: TimeHelper = mock()
    private val mId: IdHelper = mock()
    private val feature =
        NewSightingFormFeature(dataSource,
            Coordinates(LATITUDE, LONGITUDE),
            mTime,
            mId,
            DEFAULT_PICTURE)

    @Before
    fun setup() {
        `when`(mId.randomId()).thenReturn(ID)
        `when`(mTime.currentTimeMillis()).thenReturn(TIMESTAMP)
    }

    @Test
    fun `WHEN_SaveSighting_wish_is_sent_AND_complete_info_is_provided_THEN_SightingSaved_news_are_sent`() {

        val news = feature.news.wrapToObservable().test()

        feature.accept(
            NewSightingFormFeature.Wish.SaveSighting(
                TITLE,
                ADULTS,
                PUPS,
                INTERACTION,
                COMMENT
            )
        )

        news.assertValue(NewSightingFormFeature.News.SightingSaved)
    }

    @Test
    fun `WHEN_SaveSighting_wish_is_sent_AND_complete_info_is_provided_THEN_saveSighting_method_is_invoked`() {

        feature.accept(
            NewSightingFormFeature.Wish.SaveSighting(
                TITLE,
                ADULTS,
                PUPS,
                INTERACTION,
                COMMENT
            )
        )

        verify(dataSource).saveSighting(Sighting(
            id = mId.randomId(),
            heading = TITLE,
            adults = ADULTS.toInt(),
            pups = PUPS.toInt(),
            interaction = INTERACTION,
            comments = COMMENT,
            coordinates = Coordinates(LATITUDE, LONGITUDE),
            timestamp = mTime.currentTimeMillis(),
            picture = DEFAULT_PICTURE
        ))


    }

    @Test
    fun `WHEN_SaveSighting_wish_is_sent_AND_complete_info_is_not_provided_THEN_SightingNotSaved_news_are_sent`() {
        val news = feature.news.wrapToObservable().test()

        feature.accept(
            NewSightingFormFeature.Wish.SaveSighting(
                feature.state.heading,
                feature.state.adults.toString(),
                feature.state.pups.toString(),
                feature.state.interaction,
                feature.state.comments
            )
        )

        news.assertValue(NewSightingFormFeature.News.SightingNotSaved)
    }

    @Test
    fun `WHEN_UpdatePhotoUri_wish_is_sent_THEN_SightingSaved_news_are_sent`() {
//        assertThat(feature.state).isEqualTo(NewSightingFormFeature.State(picture = DEFAULT_PICTURE))
//        feature.accept(NewSightingFormFeature.Wish.UpdatePhotoUri(PICTURE))
//        assertThat(feature.state).isEqualTo(NewSightingFormFeature.State(picture = PICTURE))

        val state = feature.wrapToObservable().test()
        feature.accept(NewSightingFormFeature.Wish.UpdatePhotoUri(PICTURE))

        state.assertValues(
            NewSightingFormFeature.State(picture = DEFAULT_PICTURE),
            NewSightingFormFeature.State(picture = PICTURE)
        )

    }

    @Test
    fun `WHEN_RequestCameraStart_wish_is_sent_THEN_CameraStarted_news_are_sent`() {
        val news = feature.news.wrapToObservable().test()

        feature.accept(NewSightingFormFeature.Wish.RequestCameraStart)

        news.assertValue(NewSightingFormFeature.News.CameraStarted)
    }

    private fun <T> ObservableSource<out T>.wrapToObservable(): Observable<T> =
        Observable.wrap(cast())

    inline fun <reified T> Any?.cast(): T = this as T

    companion object {
        private const val ID = "c0b7ecba-aa37-4d2e-b5ba-8077387df797"
        private const val TITLE = "title"
        private const val ADULTS = "2"
        private const val PUPS = "1"
        private const val COMMENT = "comment"
        private const val INTERACTION = false
        private const val LATITUDE = 41.409428
        private const val LONGITUDE = 2.111255
        private const val TIMESTAMP = 1657704837919
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val PICTURE = "PICTURE"
    }
}

