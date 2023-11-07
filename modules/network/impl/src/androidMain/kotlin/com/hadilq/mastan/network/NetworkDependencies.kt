package com.hadilq.mastan.network

import com.hadilq.mastan.logictreearchitecture.di.ImplDependencies
import com.hadilq.mastan.auth.AccessTokenRequest
import okhttp3.OkHttpClient

@ImplDependencies
interface NetworkDependencies {
    val okHttpClient: OkHttpClient
    val realApi: RealApi
    fun realUserApi(accessTokenRequest: AccessTokenRequest): RealUserApi
}
