package duylt.mobile.app.ecommerce.virgomusic.presentation.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.VirgoMusicApplication
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.broadcast.NotificationReceiver
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.PlayerActivity

class MusicService : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable

    private var musicBinder = MusicBinder()
    private val idForegroundDefault = 7
    private val requestCodePendingIntentDefault = 9

    var mediaPlayer: MediaPlayer? = null

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "Virgo Music")
        return musicBinder
    }

    inner class MusicBinder : Binder() {
        fun currentService() = this@MusicService
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(iconPauseAndPlay: Int) {
        val prevIntent =
            Intent(baseContext, NotificationReceiver::class.java)
                .setAction(VirgoMusicApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            requestCodePendingIntentDefault,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java)
                .setAction(VirgoMusicApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            requestCodePendingIntentDefault,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java)
                .setAction(VirgoMusicApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            requestCodePendingIntentDefault,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java)
                .setAction(VirgoMusicApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            requestCodePendingIntentDefault,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification =
            NotificationCompat.Builder(baseContext, VirgoMusicApplication.CHANNEL_ID)
                .setContentTitle(PlayerActivity.songCurrent?.title ?: "")
                .setContentText(PlayerActivity.songCurrent?.artist ?: "")
                .setSmallIcon(R.drawable.icon_music_note)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.icon_previous, "Previous", prevPendingIntent)
                .addAction(iconPauseAndPlay, "Play", playPendingIntent)
                .addAction(R.drawable.icon_next, "Next", nextPendingIntent)
                .addAction(R.drawable.icon_clear, "Exit", exitPendingIntent)
                .build()

        startForeground(idForegroundDefault, notification)
    }

    fun startSongProgress() {
        runnable = Runnable {
            PlayerActivity.bindingScreen.textTimeStartSong.text =
                Song().formatDuration(PlayerActivity.mediaPlayer?.currentPosition?.toLong() ?: 0L)
            PlayerActivity.bindingScreen.songProgress.progress = PlayerActivity.mediaPlayer?.currentPosition ?: 0
            Handler(Looper.getMainLooper()).postDelayed(runnable, 70)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}