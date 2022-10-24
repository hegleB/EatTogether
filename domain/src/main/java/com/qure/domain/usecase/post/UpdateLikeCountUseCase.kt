package com.qure.domain.usecase.post

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class UpdateLikeCountUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(likeList : ArrayList<String>, postKey : String) = postRepository.updateLike(likeList, postKey)
}