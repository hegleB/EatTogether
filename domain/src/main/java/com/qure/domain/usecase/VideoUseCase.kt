package com.qure.domain.usecase

import android.util.Log
import androidx.paging.PagingData

import com.qure.domain.model.Items
import com.qure.domain.repository.YoutubeVideoRepository
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class VideoUseCase @Inject constructor(
    private val youtubeVideoRepository: YoutubeVideoRepository
) {
    suspend fun getYoutubeVideo(): Flow<PagingData<Items>> {
        return youtubeVideoRepository.getYoutubeVideo()
    }
}