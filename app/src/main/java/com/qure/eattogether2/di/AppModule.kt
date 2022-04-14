package com.qure.eattogether2.di

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

import com.qure.eattogether2.R
import com.qure.eattogether2.data.remote.FirebaseSource
import com.qure.eattogether2.prefs.UserPrefrence
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.intellij.lang.annotations.PrintFormat
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideGso(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    @Provides
    fun provideSigninClient(@ApplicationContext context: Context, gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(context, gso)

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providesUserPrefs(@ApplicationContext context: Context): UserPrefrence =
        UserPrefrence(context)


    @Provides
    fun providesFirebaseSource(auth: FirebaseAuth): FirebaseSource = FirebaseSource(auth)


    @Provides
    @Singleton
    fun providesFiresotre() = FirebaseFirestore.getInstance()


    @Provides
    fun providesFirebaseStorage() = FirebaseStorage.getInstance()


}