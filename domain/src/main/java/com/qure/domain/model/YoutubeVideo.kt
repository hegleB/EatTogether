package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class YoutubeVideo(
    val kind: String,
    val etag: String,
    val nextPageToken: String,
    val prevPageToken: String,
    val pageInfo: PageInfo,
    val items: List<Items>,
) : Parcelable

@Parcelize
data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int,
) : Parcelable

@Parcelize
data class Items(
    val id: String = "",
    val snippet: Snippet = Snippet(),
) : Parcelable

@Parcelize
data class Snippet(
    val publishedAt: String = "",
    val channelId: String = "",
    val title: String = "",
    val description: String = "",
    val thumbnails: ThumbNail = ThumbNail(),
    val publishTime: String = "",
    val channelTitle: String = "",
    val resourceId: ResourceId = ResourceId(),
    val videoOwnerChannelTitle: String = "",
) : Parcelable

@Parcelize
data class ResourceId(
    val kind: String = "",
    val videoId: String = "",
) : Parcelable

@Parcelize
data class ThumbNail(
    val high: High = High(),
) : Parcelable

@Parcelize
data class High(
    val url: String = "",
    val width: Int = 0,
    val height: Int = 0,
) : Parcelable
