package com.gathering.android.notif

import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class FCMNotificationService @Inject constructor(private val sharedPreferences: SharedPreferences) :
    FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO show notification
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //TODO Call API from backend
    }
}