package com.udacity

import android.Manifest.*
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.*
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding

@Suppress("PrivatePropertyName")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var URL = ""
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var customButton: LoadingButton
    private lateinit var contentMain: ContentMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        binding.toolbar.title = getString(R.string.app_name)
        customButton = binding.contentMain.customButton
        contentMain = binding.contentMain
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) {
                    makeText(this, getString(R.string.please_grant_permission), LENGTH_SHORT).show()
                } else customButton.callOnClick()
            }

        customButton.setOnClickListener {
            val url = downloadFromSource()
            if (url != null) download(url)
        }
        createNotificationChannel(getString(R.string.channel_id), getString(R.string.channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            customButton.changeButtonState(ButtonState.Loading)

            if (downloadID == id) {
                downloadNotification()
                customButton.changeButtonState(ButtonState.Completed)
            }
        }
    }

    private fun downloadNotification() {
        notificationManager = getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelAll()
        notificationManager.sendNotification(
            when (URL) {
                GLIDE -> GLIDE_LABEL
                LOAD_APP -> LOAD_APP_LABEL
                else -> RETROFIT_LABEL
            }, this
        )
    }

    private fun downloadFromSource(): String? {
        return when (contentMain.group.checkedRadioButtonId) {
            contentMain.glide.id -> GLIDE
            contentMain.c3.id -> LOAD_APP
            contentMain.retrofit.id -> RETROFIT
            else -> {
                makeText(this, getString(R.string.please_select_file), LENGTH_SHORT).show()
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun download(url: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                downloadHandler(url)
            } else requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
        } else downloadHandler(url)
    }

    private fun downloadHandler(url: String) {
        customButton.changeButtonState(ButtonState.Clicked)
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
            val notificationChannel = NotificationChannel(channelID, channelName, IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManger = this.getSystemService(NotificationManager::class.java)
            notificationManger.createNotificationChannel(notificationChannel)
        }
    }
}
