package com.qure.domain.repository

import com.qure.domain.model.User
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias AddUser = Resource<Boolean, String>
typealias UserResource = Resource<User, String>
typealias UsersResource = Resource<List<User>, String>
typealias UpdateUser = Resource<Boolean, String>

interface UserRepository {
    suspend fun setUser(uid: String, user: User): AddUser
    suspend fun getUser(uid: String): Flow<UserResource>
    suspend fun getAllUser(): Flow<UsersResource>
    suspend fun updateUser(
        uid: String,
        name: String,
        msg: String,
        image: String
    ): UpdateUser
}