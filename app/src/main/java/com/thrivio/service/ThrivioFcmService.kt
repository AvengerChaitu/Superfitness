package com.thrivio.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thrivio.network.SupabaseClient
import io.github.jan.tennert.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThrivioFcmService : FirebaseMessagingService() {

    private val fcmScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "thrivio_notifications"
        private const val CHANNEL_NAME = "Thrivio Reminders"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Push the new FCM token to Supabase device_tokens table
        fcmScope.launch {
            try {
                // SupabaseClient.client.postgrest["device_tokens"].insert(DeviceToken(fcm_token = token))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Thrivio Notification"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Aero has an update for you!"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }
}
