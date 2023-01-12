package com.qure.domain.usecase.post

import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class GetCategoryPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(categoryName: String) = postRepository.getCategoryPost(categoryName)
}