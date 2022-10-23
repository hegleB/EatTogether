package com.qure.domain.usecase

import com.qure.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, name: String, msg: String, image: String) =
        userRepository.updateUser(uid, name, msg, image)
}