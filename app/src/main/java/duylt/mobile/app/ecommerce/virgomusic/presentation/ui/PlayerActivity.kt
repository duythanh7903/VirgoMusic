package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPlayerBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.service.MusicService
import duylt.mobile.app.ecommerce.virgomusic.utils.logger
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2

class PlayerActivity : BaseActivity<ActivityPlayerBinding>(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        var musicService: MusicService? = null
        var mediaPlayer: MediaPlayer? = null
        var songCurrent: Song? = null

        var indexSong = 0

        @SuppressLint("StaticFieldLeak")
        lateinit var bindingScreen: ActivityPlayerBinding
        lateinit var songs: MutableList<Song>
        lateinit var context: PlayerActivity
    }

    override fun inflateViewBinding(): ActivityPlayerBinding =
        ActivityPlayerBinding.inflate(layoutInflater)

    override fun initView() {
        binding.textSongName.isSelected = true
        bindingScreen = binding
        context = this@PlayerActivity
        binding.iconLoop.isActivated = false

        // get intent data
        indexSong = intent.getIntExtra("current_index_song", 0)
        val isShuffle = intent.getBooleanExtra("is_shuffle", false)
        songs = MainActivity.songs
        if (isShuffle) songs.shuffle()
        // get song current
        songCurrent = getSongByIndex(indexSong)
        initMusicService()
        // init media player
        mediaPlayer = musicService?.mediaPlayer
        setLayout(songCurrent!!)
        createMediaPlayerAndPlay(songCurrent!!)
    }

    override fun setUpListener() {
        binding.apply {
            iconBack.setClickAffect2 { finish() }

            buttonPausePlay.setClickAffect2 {
                it!!.isActivated = !it.isActivated
                val isPlaying = it.isActivated
                if (isPlaying) playMusic() else pauseMusic()
            }

            buttonNext.setOnClickListener {
                indexSong++
                songCurrent = getSongByIndex(indexSong)
                createMediaPlayerAndPlay(songCurrent!!)
                setLayout(songCurrent!!)
                musicService!!.showNotification(R.drawable.icon_pause)
            }

            buttonPrevious.setOnClickListener {
                indexSong--
                songCurrent = getSongByIndex(indexSong)
                createMediaPlayerAndPlay(songCurrent!!)
                musicService!!.showNotification(R.drawable.icon_pause)
                try { setLayout(songCurrent!!) } catch (_: Exception) { }
            }

            songProgress.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    mediaPlayer?.seekTo(p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) = Unit

                override fun onStopTrackingTouch(p0: SeekBar?) = Unit
            })

            iconLoop.setClickAffect2 {
                binding.iconLoop.isActivated = !binding.iconLoop.isActivated
//                SharePrefUtils.isRepeat = binding.iconLoop.isActivated
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MusicBinder
        musicService = binder.currentService()
        if (songCurrent != null) createMediaPlayerAndPlay(songCurrent!!)
        musicService!!.showNotification(
            if (mediaPlayer!!.isPlaying) R.drawable.icon_pause
            else R.drawable.icon_play
        )
        musicService!!.startSongProgress()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        val isRepeat = binding.iconLoop.isActivated
        if (!isRepeat) { indexSong++ }
        logger("Check Is Repeat On Complete: $isRepeat")
        logger("Check Index Song Current: $indexSong")
        songCurrent = getSongByIndex(indexSong)
        try { setLayout(songCurrent!!) } catch (_: Exception) {}
        createMediaPlayerAndPlay(songCurrent!!)
        musicService!!.showNotification(R.drawable.icon_pause)
    }

    private fun setLayout(song: Song) {
        binding.apply {
            Glide.with(context).load(song.artUri)
                .apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
                .into(songImage)
            textSongName.text = song.title
            textTimeEndSong.text = song.formatDuration(song.duration)
        }
    }

    private fun createMediaPlayerAndPlay(song: Song) {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.apply {
                reset()
                setDataSource(song.path)
                prepare()
                start()
                binding.buttonPausePlay.isActivated = true
            }
            binding.textTimeStartSong.text =
                songCurrent?.formatDuration(mediaPlayer?.duration?.toLong() ?: 0L)
            binding.songProgress.progress = 0
            binding.songProgress.max = mediaPlayer?.duration!!
            mediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
            logger(e.message.toString())
            binding.buttonPausePlay.isActivated = false
        }
    }

    private fun playMusic() {
        binding.buttonPausePlay.isActivated = true
        mediaPlayer?.start()
        musicService!!.showNotification(R.drawable.icon_pause)
    }

    private fun pauseMusic() {
        binding.buttonPausePlay.isActivated = false
        mediaPlayer?.pause()
        musicService!!.showNotification(R.drawable.icon_play)
    }

    private fun getSongByIndex(index: Int): Song =
        if (index >= songs.size) {
            indexSong = 0
            songs[0]
        }
        else if (index <= -1) {
            indexSong = songs.size - 1
            songs[songs.size - 1]
        }
        else { songs[index] }

    private fun initMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }
}