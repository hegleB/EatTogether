package com.qure.domain.usecase.post

import com.qure.domain.model.PostModel
import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class SetPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(post : PostModel.Post) = postRepository.setPost(post)
}