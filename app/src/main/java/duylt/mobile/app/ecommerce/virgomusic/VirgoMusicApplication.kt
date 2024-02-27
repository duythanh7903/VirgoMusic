package duylt.mobile.app.ecommerce.virgomusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils

class VirgoMusicApplication: Application() {

    companion object {
        const val CHANNEL_ID = "channel_1"
        const val PLAY = "play"
        const val PAUSE = "pause"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()

        SharePrefUtils.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =  NotificationChannel(CHANNEL_ID, "Now is playing", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "This is an important channel for showing song!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}