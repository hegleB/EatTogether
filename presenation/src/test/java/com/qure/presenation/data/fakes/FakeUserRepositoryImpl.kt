package com.qure.presenation.data.fakes

import com.qure.domain.model.User
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepositoryImpl : UserRepository {

    private val users = listOf(
        User("abc@a.com", "abc","abc"),
        User("a@a.com", "a", "a"),
        User("abs@abs.com", "abs", "abs")
    )

    override suspend fun setUser(uid: String, user: User): AddUser {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getUser(uid: String): Flow<UserResource> {
        val filteredUsers = users.findLast { it.isSameUid(uid) } ?: User()
        return flowOf(Resource.Success(filteredUsers))

    }

    override suspend fun getAllUser(): Flow<UsersResource> {
        return flowOf(Resource.Success(users))
    }

    override suspend fun updateUser(
        uid: String,
        name: String,
        msg: String,
        image: String
    ): UpdateUser {
        val filteredUsers = users.find { it.isSameUid(uid) }
        return if (filteredUsers == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}