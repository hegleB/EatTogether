package com.qure.domain.usecase.profile

import com.qure.domain.model.User
import com.qure.domain.repository.UserRepository
import javax.inject.Inject

class SetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, user: User) = userRepository.setUser(uid, user)
}