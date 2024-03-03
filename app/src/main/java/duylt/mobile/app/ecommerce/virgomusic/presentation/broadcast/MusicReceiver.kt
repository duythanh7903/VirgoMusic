package duylt.mobile.app.ecommerce.virgomusic.presentation.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.VirgoMusicApplication
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.PlayerMusicActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.fra.NowPlayingFragment
import kotlin.system.exitProcess

class MusicReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        when (p1?.action ?: VirgoMusicApplication.EXIT) {
            VirgoMusicApplication.PREVIOUS -> {
                changeSongCurrent(false, p0)
            }

            VirgoMusicApplication.NEXT -> {
                changeSongCurrent(true, p0)
            }

            VirgoMusicApplication.PLAY -> {
                val service = PlayerMusicActivity.musicService!!
                val exo = service.exoPlayer!!
                val binding = PlayerMusicActivity.mBinding!!
                val isPlaying = exo.isPlaying
                binding.buttonPlay.isActivated = !isPlaying
                NowPlayingFragment.mBinding.buttonPlay.isActivated = !isPlaying
                if (isPlaying) {
                    exo.pause()
                    service.showNotification(R.drawable.icon_play)
                } else {
                    exo.play()
                    service.showNotification(R.drawable.icon_pause)
                }
            }

            VirgoMusicApplication.EXIT -> {
                PlayerMusicActivity.musicService?.stopForeground(Service.STOP_FOREGROUND_REMOVE)
                PlayerMusicActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun changeSongCurrent(isNextSong: Boolean, context: Context?) {
        if (context == null) return

        /* Step 1: Get Song Next Or Prev */
        PlayerMusicActivity.indexSong =
            if (isNextSong) {
                val indexTemp = ++PlayerMusicActivity.indexSong
                val isOverListSizeMax = indexTemp >= PlayerMusicActivity.songs.size
                if (isOverListSizeMax) 0 else indexTemp
            } else {
                val indexTemp = --PlayerMusicActivity.indexSong
                val isLowerListSizeMin = indexTemp <= -1
                if (isLowerListSizeMin) PlayerMusicActivity.songs.size - 1 else indexTemp
            }
        PlayerMusicActivity.songCurrent = PlayerMusicActivity.songs[PlayerMusicActivity.indexSong]
        val songCurrent = PlayerMusicActivity.songCurrent!!

        /* Step 2: Show song's information */
        PlayerMusicActivity.mBinding!!.apply {
            textSongName.text = songCurrent.title
            textTimeEnd.text = songCurrent.formatDuration(songCurrent.duration)
        }

        /* Step 3: Handle Play New Song */
        try {
            // Step 3.1: Reset exo player
            PlayerMusicActivity.musicService!!.exoPlayer?.apply {
                stop()
                release()
            }
            PlayerMusicActivity.musicService!!.exoPlayer = null

            // Step 3.2: Init exo player and Handle play new song
            val mediaItem = MediaItem.fromUri(songCurrent.path)
            PlayerMusicActivity.musicService!!.exoPlayer =
                ExoPlayer.Builder(context).build()
            PlayerMusicActivity.musicService!!.exoPlayer!!.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
                addListener(object : Player.Listener {
                    @Deprecated("Deprecated in Java")
                    override fun onPlayerStateChanged(
                        playWhenReady: Boolean,
                        @Player.State state: Int
                    ) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            changeSongCurrent(true, context)
                        }
                    }
                })
            }
        } catch (_: Exception) {
        }

        /* Step 4: Change notification's UI */
        PlayerMusicActivity.musicService!!.showNotification(R.drawable.icon_pause)

        /* Step 5: Change Fragment Now Playing */
        NowPlayingFragment.mBinding.apply {
            songCurrent.apply {
                textSongName.text = title
                textAuthorName.text = artist
                buttonPlay.isActivated = true
            }
        }
    }
}