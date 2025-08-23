package com.dkapps.grokchef.di

import com.dkapps.grokchef.BuildConfig
import com.dkapps.grokchef.data.api.XApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val X_BASE_URL = "https://api.x.ai"

    @Provides
    @Singleton
    fun provideAuthInterceptor() = Interceptor { chain ->
        val request = chain.request()
        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.XAI_INITIAL_API_KEY}")
            .build()
        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor) = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(X_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideXApiService(retrofit: Retrofit) = retrofit.create(XApiService::class.java)
}