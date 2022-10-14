package com.qure.data.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

const val STARTING_KEY = 1
const val DELAY_MILLIS = 1_000L


abstract class BasePagingSource<T: Any> : PagingSource<Int, T>() {

    lateinit var dataList : List<T>

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val position = params.key ?: STARTING_KEY
        if (position != STARTING_KEY) delay(DELAY_MILLIS)

        return try {

            LoadResult.Page(
                data = dataList,
                prevKey = if (position == STARTING_KEY) null else position - 1,
                nextKey = if (dataList.isEmpty()) null else position + 1
            )
        } catch (e: Throwable) {
            return LoadResult.Error(e)
        }

    }

}