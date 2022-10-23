package com.qure.domain.usecase


import com.qure.domain.model.User
import com.qure.domain.repository.UserRepository
import javax.inject.Inject

class GetAllUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user : User) = userRepository.getAllUser(user)
}