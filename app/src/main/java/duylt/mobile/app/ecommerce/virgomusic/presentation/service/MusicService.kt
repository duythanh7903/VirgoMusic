package duylt.mobile.app.ecommerce.virgomusic.presentation.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.VirgoMusicApplication
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.broadcast.MusicReceiver
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.PlayerMusicActivity
import java.util.concurrent.TimeUnit

class MusicService : Service() {

    private val binder by lazy { MusicBinder() }
    private val TAG_MEDIA_SESSION_MUSIC by lazy { "VIRGO_MUSIC" }
    private val RQ_CODE_PENDING_INTENT by lazy { 7 }
    private val ID_FOREGROUND by lazy { 7 }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    private lateinit var runnableTimer: Runnable
    private lateinit var handleTimer: Handler

    var exoPlayer: ExoPlayer? = null

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, TAG_MEDIA_SESSION_MUSIC)
        return binder
    }

    inner class MusicBinder : Binder() {
        fun getCurrentService() = this@MusicService
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(iconPauseAndPlay: Int) {
        val prevIntent =
            Intent(baseContext, MusicReceiver::class.java)
                .setAction(VirgoMusicApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            RQ_CODE_PENDING_INTENT,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playIntent =
            Intent(baseContext, MusicReceiver::class.java)
                .setAction(VirgoMusicApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            RQ_CODE_PENDING_INTENT,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextIntent =
            Intent(baseContext, MusicReceiver::class.java)
                .setAction(VirgoMusicApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            RQ_CODE_PENDING_INTENT,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val exitIntent =
            Intent(baseContext, MusicReceiver::class.java)
                .setAction(VirgoMusicApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            RQ_CODE_PENDING_INTENT,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(baseContext, VirgoMusicApplication.CHANNEL_ID)
                .setContentTitle(PlayerMusicActivity.songCurrent?.title ?: "")
                .setContentText(PlayerMusicActivity.songCurrent?.artist ?: "")
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

        startForeground(ID_FOREGROUND, notification)
    }

    fun startProgress() {
        runnable = Runnable {
            PlayerMusicActivity.mBinding!!.textTimeStart.text =
                Song().formatDuration(PlayerMusicActivity.musicService!!.exoPlayer!!.currentPosition)
            PlayerMusicActivity.mBinding!!.progressSong.apply {
                max = PlayerMusicActivity.musicService!!.exoPlayer!!.duration.toInt()
                progress = PlayerMusicActivity.musicService!!.exoPlayer!!.currentPosition.toInt()
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    fun handleTimer(time: Int) {
        if (time != 0) {
            runnableTimer = Runnable {
                PlayerMusicActivity.apply {
                    musicService!!.exoPlayer!!.pause()
                    mBinding!!.buttonPlay.isActivated = false
                }
            }
            handleTimer = Handler(Looper.getMainLooper())
            handleTimer.postDelayed(runnableTimer, TimeUnit.SECONDS.toMillis(time.toLong() * 60L))
        }
    }

    fun cancelTimer() {
        try {
            handleTimer.removeCallbacks(runnableTimer)
        } catch (_: Exception) {}
    }
}