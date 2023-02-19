package com.qure.data.repository

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.PostModel
import com.qure.domain.repository.FirebaseStorageRepository
import com.qure.domain.utils.IMAGES_COLLECTION_GROUP
import com.qure.domain.utils.POSTS_COLLECTION_PATH
import com.qure.domain.utils.POST_IMAGES_FIELD
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageRepository {
    override suspend fun uploadImage(
        uploadPath: String,
        key: String,
        imageId: Int,
        imageUri: Uri
    ): UploadTask.TaskSnapshot =
        when (imageId) {
            -1 -> {
                firebaseStorage.getReference()
                    .child(uploadPath + key + ".jpg")
                    .putFile(imageUri)
                    .await()
            }
            else -> {
                firebaseStorage.getReference()
                    .child(uploadPath + key + "/" + imageId + ".jpg")
                    .putFile(imageUri)
                    .await()
            }
        }


    override suspend fun downloadImage(uploadPath: String, key: String, imageId: Int): Uri =
        when (imageId) {
            -1 -> {
                firebaseStorage.getReference()
                    .child(uploadPath + key + ".jpg")
                    .downloadUrl
                    .await()
            }
            else -> {
                firebaseStorage.getReference()
                    .child(uploadPath + key + "/" + imageId + ".jpg")
                    .downloadUrl
                    .await()
            }
        }

    override suspend fun updateDownloadImageUri(collectionPath: String,imagesField: String, uri: Uri, key: String) =
        callbackFlow {
            trySend(Resource.Loading())
            val callback = firestore.collection(collectionPath)
                .document(key)
                .update(imagesField, FieldValue.arrayUnion(uri))
                .addOnCompleteListener {
                    val updatedImageResource = if (it.isSuccessful) {
                        Resource.Success(true)
                    } else {
                        Resource.Error("update error")
                    }
                    trySend(updatedImageResource)
                }
            awaitClose {
                callback.isCanceled
            }
        }

    override suspend fun setDownloadImage(postImage: PostModel.PostImage) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH).document(postImage.postkey)
            .collection(IMAGES_COLLECTION_GROUP)
            .document()
            .set(PostModel.PostImage(postImage.postkey, postImage.postImage))
            .addOnCompleteListener {
                val downloadImageResource = if (it.isSuccessful) {
                    Resource.Success(true)
                } else {
                    Resource.Error("set post image error")
                }
                trySend(downloadImageResource)
            }
        awaitClose {
            callback.isCanceled
        }
    }
}