package duylt.mobile.app.ecommerce.virgomusic.presentation.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.VirgoMusicApplication
import duylt.mobile.app.ecommerce.virgomusic.presentation.service.MusicService
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.PlayerActivity
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when (p1?.action) {
            VirgoMusicApplication.PREVIOUS -> {
                nextPrevSong(false, p0 ?: PlayerActivity.context)
            }

            VirgoMusicApplication.NEXT -> {
                nextPrevSong(true, p0 ?: PlayerActivity.context)
            }

            VirgoMusicApplication.PLAY -> {
                if (PlayerActivity.mediaPlayer!!.isPlaying) pauseMusic() else playMusic()
            }

            VirgoMusicApplication.EXIT -> {
                PlayerActivity.musicService?.stopForeground(Service.STOP_FOREGROUND_REMOVE)
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.mediaPlayer?.start()
        PlayerActivity.musicService?.showNotification(R.drawable.icon_pause)
        PlayerActivity.bindingScreen.buttonPausePlay.isActivated = true
    }

    private fun pauseMusic() {
        PlayerActivity.mediaPlayer?.pause()
        PlayerActivity.musicService?.showNotification(R.drawable.icon_play)
        PlayerActivity.bindingScreen.buttonPausePlay.isActivated = false
    }

    private fun nextPrevSong(isNextSong: Boolean, context: Context) {
        if (isNextSong) {
            PlayerActivity.indexSong++
            PlayerActivity.indexSong =
                if (PlayerActivity.indexSong >= PlayerActivity.songs.size) 0
                else PlayerActivity.indexSong
        } else {
            PlayerActivity.indexSong--
            PlayerActivity.indexSong =
                if (PlayerActivity.indexSong <= -1) PlayerActivity.songs.size - 1
                else PlayerActivity.indexSong
        }
        PlayerActivity.songCurrent = PlayerActivity.songs[PlayerActivity.indexSong]
        PlayerActivity.mediaPlayer?.apply {
            if (this == null) PlayerActivity.mediaPlayer = MediaPlayer()
            reset()
            setDataSource(PlayerActivity.songCurrent!!.path)
            prepare()
            start()
            PlayerActivity.bindingScreen.buttonPausePlay.isActivated = true
        }
        PlayerActivity.bindingScreen.apply {
            val song = PlayerActivity.songCurrent!!
            Glide.with(context).load(song.artUri)
                .apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
                .into(songImage)
            textSongName.text = song.title
            textTimeEndSong.text = song.formatDuration(song.duration)
        }
        PlayerActivity.musicService!!.showNotification(R.drawable.icon_pause)
    }
}