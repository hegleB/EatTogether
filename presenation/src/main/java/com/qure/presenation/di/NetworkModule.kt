package com.qure.presenation.di

import android.content.Context
import android.provider.Settings.Secure.getString
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.qure.presenation.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.intellij.lang.annotations.PrintFormat

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideSignInIntentRequest(@ApplicationContext context: Context): GetSignInIntentRequest =
        GetSignInIntentRequest.builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

    @Provides
    fun provideSigninClient(@ApplicationContext context: Context) =
        Identity.getSignInClient(context)

    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()
}