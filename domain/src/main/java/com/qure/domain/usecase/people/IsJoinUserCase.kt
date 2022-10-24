package com.qure.domain.usecase.people

import com.google.firebase.auth.FirebaseUser
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class IsJoinUserCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun isJoin(user : FirebaseUser) = authRepository.isJoin(user)
}