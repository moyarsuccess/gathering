package com.gathering.android.common

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(private val tokenRepo: TokenRepo) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader(AUTH_HEADER_KEY, "$BEARER ${tokenRepo.getToken()}")
                .build()
        )
    }

    companion object {
        private const val AUTH_HEADER_KEY = "authorization"
        private const val BEARER = "Bearer"
    }
}