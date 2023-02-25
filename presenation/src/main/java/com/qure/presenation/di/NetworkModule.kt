package com.qure.presenation.di

import android.content.Context
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.qure.data.service.YoutubeService
import com.qure.presenation.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

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

    private val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private val TIME_OUT_COUNT : Long = 10

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideOkHttpClient(interceptor: Interceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_COUNT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_COUNT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    @Provides
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            val originalHttpUrl = chain.request().url

            request.url(
                originalHttpUrl.newBuilder()
                    .addQueryParameter("key", "AIzaSyCIWXL4iuXkzrwBTldYqpal2jMmTyrZn38")
                    .addQueryParameter("playlistId", "PLDy9iek5QK0Xzg6uXNisMyGrlzy4thDly")
                    .addQueryParameter("maxResults","50")
                    .addQueryParameter("part","snippet")
                    .build()
            )

            chain.proceed(request.build())
        }
    }

    @Provides
    fun provideMovieApi(retrofit: Retrofit) : YoutubeService {
        return retrofit.create()
    }
}
