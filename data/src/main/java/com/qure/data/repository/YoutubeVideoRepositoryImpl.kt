package com.qure.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.qure.data.pagingsource.YoutubePagingSource
import com.qure.data.service.YoutubeService
import com.qure.domain.model.Items
import com.qure.domain.repository.YoutubeVideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class YoutubeVideoRepositoryImpl @Inject constructor(
    private val youtubeService: YoutubeService
): YoutubeVideoRepository {
    override suspend fun getYoutubeVideo(): Flow<PagingData<Items>> {
        return Pager(PagingConfig(10)) {
            YoutubePagingSource(youtubeService)
        }.flow
    }
}