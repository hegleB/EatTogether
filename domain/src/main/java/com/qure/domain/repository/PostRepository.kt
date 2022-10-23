package com.qure.domain.repository


import com.qure.domain.model.PostModel
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    suspend fun getLikeCount(uid : String) : Flow<Resource<Int,String>>
    suspend fun getPostCount(uid : String) : Flow<Resource<Int, String>>
    suspend fun getAllPost() : Flow<Resource<List<PostModel.Post>,String>>
    suspend fun updateLike(likeList : ArrayList<String>, postKey : String) : Flow<Resource<String, String>>
    suspend fun checkPost(postKey: String) : Flow<Resource<PostModel.Post, String>>

}