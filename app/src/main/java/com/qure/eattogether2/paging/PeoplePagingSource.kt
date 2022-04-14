package com.qure.eattogether2.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.qure.eattogether2.data.User
import kotlinx.coroutines.tasks.await

const val STARTING_PAGE_INDEX = 1
class PeoplePagingSource(

    private val allUsers : LiveData<MutableList<User>>

) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {

        val position = params.key ?: STARTING_PAGE_INDEX

        return try{



            LoadResult.Page(
                data= allUsers.value!!,
                prevKey = if(position== STARTING_PAGE_INDEX) null else position-1,
                nextKey = if(allUsers.value!!.isEmpty()) null else null
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return null
    }


}