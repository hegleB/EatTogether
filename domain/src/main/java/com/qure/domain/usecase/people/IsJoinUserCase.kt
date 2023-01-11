package com.qure.domain.usecase.people

import com.google.firebase.auth.FirebaseUser
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class IsJoinUserCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: FirebaseUser) = authRepository.isJoin(user)
}