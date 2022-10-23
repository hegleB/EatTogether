package com.qure.domain.usecase

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class CheckPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postKey : String) = postRepository.checkPost(postKey)
}