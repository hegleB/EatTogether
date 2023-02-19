package com.qure.presenation.data.fakes

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.PostModel
import com.qure.domain.repository.FirebaseStorageRepository
import com.qure.domain.repository.SetDownLoadImages
import com.qure.domain.repository.UpdateDownloadImageResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class FakeFirebaseStorageImpl: FirebaseStorageRepository {
    override suspend fun uploadImage(
        imagePath: String,
        key: String,
        imageId: Int,
        imageUri: Uri
    ): UploadTask.TaskSnapshot {
        return FirebaseStorage.getInstance().getReference().putFile(imageUri).await()
    }

    override suspend fun downloadImage(uploadPath: String, key: String, imageId: Int): Uri {
        return Uri.EMPTY
    }

    override suspend fun updateDownloadImageUri(
        collectionPath: String,
        imageField: String,
        uri: Uri,
        key: String
    ): Flow<UpdateDownloadImageResource> {
        return flowOf()
    }

    override suspend fun setDownloadImage(postImage: PostModel.PostImage): Flow<SetDownLoadImages> {
        return flowOf()
    }
}