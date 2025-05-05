package com.mobile.wanderwallet.di

import android.content.Context
import com.mobile.wanderwallet.BuildConfig
import com.mobile.wanderwallet.data.remote.WanderWalletApiService
import com.mobile.wanderwallet.data.repository.AuthInterceptor
import com.mobile.wanderwallet.data.repository.NetworkWanderWalletApiRepository
import com.mobile.wanderwallet.data.repository.SharedPrefsTokenProvider
import com.mobile.wanderwallet.data.repository.TokenProvider
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WanderWalletModule {
    @Provides
    @Singleton
    fun provideTokenProvider(
        @ApplicationContext context: Context
    ): TokenProvider {
        return SharedPrefsTokenProvider(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenProvider: TokenProvider
    ): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val baseUrl = BuildConfig.BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): WanderWalletApiService {
        return retrofit.create(WanderWalletApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideApiRepository(
        wanderWalletApiService: WanderWalletApiService,
        tokenProvider: TokenProvider
    ): WanderWalletApiRepository {
        return NetworkWanderWalletApiRepository(wanderWalletApiService, tokenProvider)
    }
}