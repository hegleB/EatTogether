package com.qure.eattogether2.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post

class CommentsPagingSource(
    private val getAllComments: LiveData<List<Comments>>
) : PagingSource<Int, Comments>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comments> {

        val position = params.key ?: STARTING_PAGE_INDEX

        return try {

            LoadResult.Page(
                data = getAllComments.value!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position + 1,
                nextKey = if (getAllComments.value!!.isEmpty()) null else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comments>): Int? {
        return null
    }
}