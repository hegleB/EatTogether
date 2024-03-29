package com.qure.presenation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.qure.data.repository.*
import com.qure.data.service.YoutubeService
import com.qure.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
    ): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    fun providePostRepository(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
    ): PostRepository {
        return PostRepositoryImpl(firestore, firebaseStorage)
    }

    @Provides
    fun provideBarcodeRepository(
        firestore: FirebaseFirestore,
    ): BarcodeRepository {
        return BarcodeRepositoryImpl(firestore)
    }

    @Provides
    fun provideMeetingRepository(
        firestore: FirebaseFirestore,
    ): MeetingRepository {
        return MeetingRepositoryImpl(firestore)
    }

    @Provides
    fun provideSettingRepository(
        firestore: FirebaseFirestore,
    ): SettingRepository {
        return SettingRepositoryImpl(firestore)
    }

    @Provides
    fun provideCommentsRepository(
        firestore: FirebaseFirestore,
    ): CommentRepository {
        return CommentRepositoryImpl(firestore)
    }

    @Provides
    fun provideChatRepository(
        firestore: FirebaseFirestore,
    ): ChatRepository {
        return ChatRepositoryImpl(firestore)
    }

    @Provides
    fun provideFirebaseStorageRepository(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage
    ): FirebaseStorageRepository {
        return FirebaseStorageRepositoryImpl(firestore, firebaseStorage)
    }

    @Provides
    fun provideYoutubeVideoeRepository(
        youtubeService: YoutubeService,
    ): YoutubeVideoRepository {
        return YoutubeVideoRepositoryImpl(youtubeService)
    }
}
