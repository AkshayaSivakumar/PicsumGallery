package com.experiment.android.picsumgallery.di.module

import android.app.Application
import com.experiment.android.picsumgallery.BuildConfig
import com.experiment.android.picsumgallery.data.remote.PicsumApi
import com.experiment.android.picsumgallery.utils.network.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun providesNetworkConfig(context: Application): NetworkConfig {
        return NetworkConfig(context)
    }

    @Provides
    @Singleton
    fun providesOkHttpCache(
        networkConfig: NetworkConfig
    ): Cache {
        return Cache(networkConfig.getCacheDir(), networkConfig.getCacheSize())
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        else HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        networkConfig: NetworkConfig,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(networkConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
            .connectTimeout(networkConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
            .cache(cache)

        return builder.build();
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        networkConfig: NetworkConfig,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(networkConfig.getBaseUrl())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): PicsumApi {
        return retrofit.create(PicsumApi::class.java)
    }

}