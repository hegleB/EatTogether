package com.qure.domain.repository

import com.qure.domain.model.User
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow


interface UserRepository {

    suspend fun setUser(uid : String, user : User) : Flow<Resource<String, String>>
    suspend fun getUser(uid: String) : Flow<Resource<User, String>>
    suspend fun getAllUser() : Flow<Resource<List<User>, String>>
    suspend fun updateUser(uid : String, name: String, msg: String, image: String) : Flow<Resource<String, String>>
}