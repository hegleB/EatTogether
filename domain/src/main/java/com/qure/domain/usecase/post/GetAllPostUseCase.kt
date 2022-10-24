package com.qure.domain.usecase.post

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class GetAllPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke() = postRepository.getAllPost()
}