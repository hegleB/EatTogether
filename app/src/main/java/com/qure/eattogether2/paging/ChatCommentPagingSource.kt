package com.qure.eattogether2.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.ChatRoom

class ChatCommentPagingSource(
    private val getAllChat: MutableLiveData<List<ChatComment>>
) : PagingSource<Int, ChatComment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatComment> {

        val position = params.key ?: STARTING_PAGE_INDEX

        return try {

            LoadResult.Page(
                data = getAllChat.value!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position + 1,
                nextKey = if (getAllChat.value!!.isEmpty()) null else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ChatComment>): Int? {
        return null
    }
}