package com.qure.domain.usecase.post

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class GetPostCountUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(uid : String) = postRepository.getPostCount(uid)
}