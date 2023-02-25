package com.qure.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qure.data.service.YoutubeService
import com.qure.domain.model.Items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class YoutubePagingSource(
    private val service: YoutubeService,
) : PagingSource<String, Items>() {
    override fun getRefreshKey(state: PagingState<String, Items>): String? {
        var current: String? = ""
        val anchorPosition = state.anchorPosition

        CoroutineScope(Dispatchers.IO).launch {
            if (anchorPosition != null) {
                current = state.closestPageToPosition(anchorPosition)?.prevKey?.let {
                    service.getYouTubeVideos(it).nextPageToken
                }
            }
        }
        return current
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Items> {
        val start = params.key ?: ""
        return try {
            delay(1000)
            val response = service.getYouTubeVideos(start)
            val items = response.items
            val nextKey = if (items.isEmpty()) null else response.nextPageToken
            val prevKey = if (start == "") null else response.prevPageToken
            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

}