package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(body: String, applicationContext: Context) {

    val intent = Intent(applicationContext, DetailActivity::class.java)
    intent.putExtra("FILE", body)
    intent.putExtra("STATUS", applicationContext.getString(R.string.download_complete))

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val icoImg = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_assistant_black_24dp
    )

    val bigImg = NotificationCompat.BigPictureStyle()
        .bigPicture(icoImg)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(body)
        .setAutoCancel(true)
        .setStyle(bigImg)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            pendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}