package com.qure.domain.repository

import android.net.Uri
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.PostModel
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias UpdateDownloadImageResource = Resource<Boolean, String>
typealias SetDownLoadImages = Resource<Boolean, String>

interface FirebaseStorageRepository {
    suspend fun uploadImage(uploadPath: String, key: String, imageId: Int, imageUri: Uri): UploadTask.TaskSnapshot
    suspend fun downloadImage(uploadPath: String, key: String, imageId: Int): Uri
    suspend fun updateDownloadImageUri(collectionPath: String, imageField: String, uri: Uri, key: String): Flow<UpdateDownloadImageResource>
    suspend fun setDownloadImage(postImage: PostModel.PostImage): Flow<SetDownLoadImages>
}