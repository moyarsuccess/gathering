package com.gathering.android.notif

import com.gathering.android.common.GeneralApiResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseDeviceTokenChangeService {

    @POST("auth/device_token")
    suspend fun deviceTokenChanged(@Query("device_token") deviceToken: String): GeneralApiResponse
}