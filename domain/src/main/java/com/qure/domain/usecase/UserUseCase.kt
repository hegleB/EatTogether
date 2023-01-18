package com.qure.domain.usecase

import com.qure.domain.model.User
import com.qure.domain.repository.AuthRepository
import com.qure.domain.repository.MeetingRepository
import com.qure.domain.repository.UserRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val meetingRepository: MeetingRepository
) {
    suspend fun getAllUser() = userRepository.getAllUser()
    suspend fun getUserMessageToken() = authRepository.geMessageToken()
    suspend fun getUser(uid: String) = userRepository.getUser(uid)
    fun getCurrentUser() = authRepository.getCurrentUser()
    suspend fun getMeetingCount(uid: String) = meetingRepository.getMeetingCount(uid)
    suspend fun setMeetingCount(uid: String, count: Int) =
        meetingRepository.setMeetingCount(uid, count)
    suspend fun setUser(uid: String, user: User) = userRepository.setUser(uid, user)
    suspend fun updateMeetingCount(uid: String, count: Int) =
        meetingRepository.updateMeetingCount(uid, count)
    suspend fun updateUser(uid: String, name: String, msg: String, image: String) =
        userRepository.updateUser(uid, name, msg, image)
}