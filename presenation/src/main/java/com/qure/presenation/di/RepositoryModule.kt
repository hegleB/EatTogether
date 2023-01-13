package com.qure.presenation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.data.repository.*
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
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): PostRepository {
        return PostRepositoryImpl(firestore)
    }

    @Provides
    fun provideBarcodeRepository(
        firestore: FirebaseFirestore
    ): BarcodeRepository {
        return BarcodeRepositoryImpl(firestore)
    }

    @Provides
    fun provideMeetingRepository(
        firestore: FirebaseFirestore
    ): MeetingRepository {
        return MeetingRepositoryImpl(firestore)
    }

    @Provides
    fun provideSettingRepository(
        firestore: FirebaseFirestore
    ): SettingRepository {
        return SettingRepositoryImpl(firestore)
    }

    @Provides
    fun provideCommentsRepository(
        firestore: FirebaseFirestore
    ): CommentRepository {
        return CommentRepositoryImpl(firestore)
    }

    @Provides
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository {
        return ChatRepositoryImpl(firestore)
    }


}