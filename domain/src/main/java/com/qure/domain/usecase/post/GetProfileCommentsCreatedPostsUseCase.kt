package com.qure.domain.usecase.post

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class GetProfileCommentsCreatedPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(uid: String) = postRepository.getProfileCommentsCreatedPosts(uid)
}