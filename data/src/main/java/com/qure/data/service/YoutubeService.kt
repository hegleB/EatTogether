package com.qure.data.service

import com.qure.domain.model.YoutubeVideo
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {

    @GET("playlistItems")
    suspend fun getYouTubeVideos(
        @Query("pageToken") pageToken: String,
    ): YoutubeVideo
}
