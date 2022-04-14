package com.qure.eattogether2.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import kotlinx.coroutines.tasks.await

class PostPagingSource(
    private val getAllPosts : LiveData<MutableList<Post>>
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {

        val position = params.key ?: STARTING_PAGE_INDEX

        return try{

            LoadResult.Page(
                data= getAllPosts.value!!,
                prevKey = if(position== STARTING_PAGE_INDEX) null else position+1,
                nextKey = if(getAllPosts.value!!.isEmpty()) null else null
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return null
    }
}