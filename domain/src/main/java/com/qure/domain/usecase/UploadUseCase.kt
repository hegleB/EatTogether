package com.qure.domain.usecase

import android.net.Uri
import com.qure.domain.model.PostModel
import com.qure.domain.repository.FirebaseStorageRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadUseCase @Inject constructor(
    private val firebaseStorageRepository: FirebaseStorageRepository
) {
    suspend fun uploadImage(uploadPath: String, key: String, imageId: Int, imageUri: Uri) = flow {
        emit(Resource.Loading())
        try {
            val response = firebaseStorageRepository.uploadImage(uploadPath, key, imageId, imageUri)
            if (response != null) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error("response is null"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }

    }

    suspend fun downloadImage(uploadPath: String, key: String, imageId: Int) = flow {
        emit(Resource.Loading())
        try {
            val response = firebaseStorageRepository.downloadImage(uploadPath, key, imageId)
            if (response != null) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error("response is null"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    suspend fun updateDownloadImageUri(collectionPath: String, imageField: String, uri: Uri, key: String) =
        firebaseStorageRepository.updateDownloadImageUri(collectionPath,imageField, uri, key)

    suspend fun setDownloadImage(postImage: PostModel.PostImage) =
        firebaseStorageRepository.setDownloadImage(postImage)
}