package com.qure.domain.repository

import androidx.paging.PagingData
import com.qure.domain.model.Items
import kotlinx.coroutines.flow.Flow


interface YoutubeVideoRepository {
    suspend fun getYoutubeVideo(): Flow<PagingData<Items>>
}