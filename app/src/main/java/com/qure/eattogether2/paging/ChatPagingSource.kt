package com.qure.eattogether2.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.Comments

class ChatPagingSource(
    private val getAllChatroom: MutableLiveData<List<ChatRoom>>
) : PagingSource<Int, ChatRoom>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatRoom> {

        val position = params.key ?: STARTING_PAGE_INDEX

        return try {

            LoadResult.Page(
                data = getAllChatroom.value!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position + 1,
                nextKey = if (getAllChatroom.value!!.isEmpty()) null else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ChatRoom>): Int? {
        return null
    }
}