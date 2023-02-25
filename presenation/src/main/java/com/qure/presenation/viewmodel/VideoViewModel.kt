package com.qure.presenation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.qure.domain.model.Items
import com.qure.domain.usecase.VideoUseCase
import com.qure.presenation.base.BaseViewModel
import com.qure.presenation.utils.YoutubeExtractor
import com.qure.presenation.utils.YoutubeExtractorListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoUseCase: VideoUseCase,
) : BaseViewModel() {

    private val _mediaSource : MutableLiveData<MediaSource> = MutableLiveData()
    val mediaSource : LiveData<MediaSource>
        get() = _mediaSource

    private val _isFullscreen : MutableLiveData<Boolean> = MutableLiveData()
    val isFullsreen : LiveData<Boolean>
        get() = _isFullscreen

    private var currentVideoItems: Flow<PagingData<Items>>? = null

    suspend fun getYoutubeVideo(): Flow<PagingData<Items>> {
        var lastResult = currentVideoItems
        if (lastResult != null) {
            return lastResult
        }
        val newResult : Flow<PagingData<Items>> =  videoUseCase.getYoutubeVideo().cachedIn(viewModelScope)
        currentVideoItems = newResult

        return newResult
    }

    fun getYoutubeVideoId(context: Context, videoId: String) {
        val listener = object : YoutubeExtractorListener {
            override fun onExtractionComplete(url: String) {
                _mediaSource.value = buildMediaSource(url, context)
            }
        }
        YoutubeExtractor(context, listener).extract(videoId)
    }
    private fun buildMediaSource(uri: String, context: Context): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(context, "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(uri)))
    }

    fun setFullscreen(enable: Boolean) {
        _isFullscreen.value = enable
    }
}