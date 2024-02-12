package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var URL = ""
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.customButton.setOnClickListener {
            val url = downloadFromSource()
            if (url != null) {
                download(url)
                binding.contentMain.customButton.changeButtonState(ButtonState.Clicked)
            }
        }
        createNotificationChannel(getString(R.string.channel_id), getString(R.string.channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            binding.contentMain.customButton.changeButtonState(ButtonState.Loading)

            if (downloadID == id) {
                downloadNotification()
                binding.contentMain.customButton.changeButtonState(ButtonState.Completed)
            }
        }
    }

    private fun downloadNotification() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelAll()
        notificationManager.sendNotification(
            when (URL) {
                GLIDE -> "Glide"
                LOADAPP -> "Udacity File"
                else -> "Retrofit"
            }, this
        )
    }

    private fun downloadFromSource(): String? {
        return when (binding.contentMain.group.checkedRadioButtonId) {
            binding.contentMain.glide.id -> GLIDE

            binding.contentMain.c3.id -> LOADAPP

            binding.contentMain.retrofit.id -> RETROFIT

            else -> {
                Toast.makeText(this, getString(R.string.please_select_file), Toast.LENGTH_SHORT)
                    .show()
                null
            }
        }
    }

    private fun download(url: String) {
        URL = url

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID = downloadManager.enqueue(request)
    }

    private fun createNotificationChannel(channelID: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManger = this.getSystemService(NotificationManager::class.java)
            notificationManger.createNotificationChannel(notificationChannel)
        }
    }
}
