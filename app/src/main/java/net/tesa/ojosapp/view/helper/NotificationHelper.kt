package net.tesa.ojosapp.view.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.tesa.ojosapp.R

object NotificationConstants {
    const val CHANNEL_NAME = "notify_practice_channel"
    const val CHANNEL_DESCRIPTION = "notify_practice_description"
    const val CHANNEL_ID = "notify_practice_channel_123456"
    const val NOTIFICATION_ID = 1
}

object NotificationHelper {

    fun sendLocalNotification(context: Context, message: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message)

        NotificationManagerCompat.from(context)
            .notify(NotificationConstants.NOTIFICATION_ID, builder.build())
    }

    fun cancelNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NotificationConstants.NOTIFICATION_ID)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = NotificationConstants.CHANNEL_NAME
            val description = NotificationConstants.CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)

        }
    }
}