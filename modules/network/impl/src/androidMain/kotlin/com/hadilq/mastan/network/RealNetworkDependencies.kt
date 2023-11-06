package com.hadilq.mastan.network

import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.auth.AccessTokenRequest
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class RealNetworkDependencies(
    private val debug: Boolean,
    private val logicTreeArchitecture: LogicTreeArchitecture,
): NetworkDependencies {
    /**
     * It's a single instance in [RealNetworkLogicIo] scope.
     */
    override val okHttpClient: OkHttpClient by logicTreeArchitecture.singleWithNoRace {
        val builder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .cache(Cache(context.cacheDir, 1000))

        builder
            .apply {
                if (debug) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .build()
    }

    /**
     * It's a single instance in [RealNetworkLogicIo] scope.
     */
    override val realApi: RealApi by logicTreeArchitecture.singleWithNoRace {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        Retrofit
            .Builder()
            .baseUrl("https://androiddev.social")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build().create(RealApi::class.java)

    }

    /**
     * It's a single instance in [RealNetworkLogicIo] scope.
     */
    override fun realUserApi(
        accessTokenRequest: AccessTokenRequest,
    ): RealUserApi {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl("https://${accessTokenRequest.domain}")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build().create(RealUserApi::class.java)
    }
}