package com.rodrigoloq.chatapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rodrigoloq.chatapp.MainActivity
import com.rodrigoloq.chatapp.R
import java.util.Random

class MyFcmService : FirebaseMessagingService() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    companion object{
        private const val NOTIFICATION_CHANNEL_ID = "CHAT_APP_CHANNEL_ID"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val myUid = firebaseAuth.uid
        if (myUid != null) {
            val hashMap = HashMap<String, Any>()
            hashMap["fcmToken"] = token

            val ref = FirebaseDatabase.getInstance().getReference("users")
            ref.child(myUid)
                .updateChildren(hashMap)
                .addOnCompleteListener {  }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(message.notification != null){
            val senderUid = message.data["senderUid"]

            showNotification(
                message.notification?.title,
                message.notification?.body,
                senderUid ?: "Desconocido"
            )
        }
    }

    private fun showNotification(title: String?, body: String?, senderUid: String) {
        val notificationId = Random().nextInt(3000)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        configNotificationChannel(notificationManager)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", senderUid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent
            .getActivity(this,
                0, intent,
                PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.chat_notification)
            .setContentTitle(title ?: "Sin titulo")
            .setContentText(body ?: "Sin contenido")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId,notificationBuilder.build())
    }

    private fun configNotificationChannel(notificationManager: NotificationManager) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chat_Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Show Chat Notification"
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}