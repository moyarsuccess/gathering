package com.gathering.android.notif

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gathering.android.R
import com.gathering.android.navhost.NavHostActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class FCMNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, NavHostActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(KEY_EVENT_ID, message.eventId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.gatherz_high_resolution_logo_transparent)
            .setContentTitle(message.title)
            .setContentText(message.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        createNotificationChannel()
        with(NotificationManagerCompat.from(this)) {
            if (PermissionX.isGranted(
                    this@FCMNotificationService,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        coroutineScope.launch {
            try {
                firebaseRepository.deviceTokenChanged(token)
            } catch (exception: Exception) {
                Log.d("FCMNotificationService", exception.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private val RemoteMessage.title: String
        get() = this.data.getOrDefault(KEY_TITLE, "")

    private val RemoteMessage.body: JSONObject
        get() = JSONObject(this.data.getOrDefault(KEY_BODY, ""))

    private val RemoteMessage.message: String
        get() = body.optString(KEY_MESSAGE)

    private val RemoteMessage.eventId: Long
        get() = body.optLong(KEY_EVENT_ID)

    companion object {
        private const val CHANNEL_ID = "gatherz_invitation_notification"
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_MESSAGE = "message"
        const val KEY_EVENT_ID = "event_id"
    }
}