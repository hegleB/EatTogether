package com.qure.presenation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qure.domain.model.Items
import com.qure.domain.usecase.VideoUseCase
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoUseCase: VideoUseCase,
) : BaseViewModel() {
    suspend fun getYoutubeVideo(): Flow<PagingData<Items>> {
        return videoUseCase.getYoutubeVideo()
            .cachedIn(viewModelScope)
    }
}