package com.example.reminder

import android.R
import android.app.Notification
import android.content.BroadcastReceiver
import androidx.core.app.NotificationCompat

import android.app.NotificationChannel

import android.app.NotificationManager

import android.os.Build

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri


class AlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "CHANNEL_SAMPLE"

    override fun onReceive(context: Context, intent: Intent) {

        fun playtouchingsquares() { //audio for touching square
            soundPoolAudio.play(audio ,1f, 1f, 4, 0, 2f)
        }

        // Get id & message
        val notificationId = intent.getIntExtra("notification", 0)
        val message = intent.getStringExtra("todo")

        // Call MainActivity when notification is tapped.
        val mainIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0)

        // NotificationManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            val channelName: CharSequence = "My Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Prepare Notification
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentText(message)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(alarmSound)

        var notification: Notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        notificationManager.notify(notificationId, notification)

    }
}