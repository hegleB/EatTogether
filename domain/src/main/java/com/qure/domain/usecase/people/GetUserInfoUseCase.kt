package com.qure.domain.usecase.people

import com.qure.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String) = userRepository.getUser(uid)
}