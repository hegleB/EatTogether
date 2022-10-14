package com.qure.presenation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.data.repository.AuthRepositoryImpl
import com.qure.data.repository.FireStoreRepositoryImpl
import com.qure.domain.repository.AuthRepository
import com.qure.domain.repository.FireStoreRepository
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
    ) : AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    fun provideFireStoreRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) : FireStoreRepository {
        return FireStoreRepositoryImpl(firestore,firebaseAuth)
    }
}