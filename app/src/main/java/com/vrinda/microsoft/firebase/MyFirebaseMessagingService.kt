package com.vrinda.microsoft.firebase

import com.vrinda.microsoft.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vrinda.microsoft.general.ClassNotesActivity
import com.vrinda.microsoft.general.ViewNotificationsActivity
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val ADMIN_CHANNEL_ID = "admin_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        // Get Message details
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        var intent: Intent? = null

        if(title!!.contains("Uploaded a File!")) {
           intent = Intent(this, ClassNotesActivity::class.java)
        } else {
           intent = Intent(this, ViewNotificationsActivity::class.java)

        }

        /*
         Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
         to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logo)

        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_large)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.data["message"]))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)

        //Set notification color to match your app color template
        notificationBuilder.setSmallIcon(R.drawable.logo_large)
        notificationBuilder.color = resources.getColor(R.color.colorPrimary)
        notificationManager.notify(notificationID, notificationBuilder.build())
   }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to device notification"
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }

    companion object {
        private const val TAG = "mFirebaseIIDService"
    }
}

