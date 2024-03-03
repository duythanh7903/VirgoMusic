package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb.AppDatabase
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPlayerMusicBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.domain.repository.FavoriteRepo
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.factory.FavoriteFactory
import duylt.mobile.app.ecommerce.virgomusic.presentation.service.MusicService
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.fra.NowPlayingFragment
import duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel.FavoriteViewModel
import duylt.mobile.app.ecommerce.virgomusic.utils.*

class PlayerMusicActivity : BaseActivity<ActivityPlayerMusicBinding>(), ServiceConnection {

    private var isShuffle = false
    private val INDEX_TIME_OFF = 0
    private val INDEX_TIME_15m = 1
    private val INDEX_TIME_30m = 2
    private val INDEX_TIME_60m = 3
    private val TIMER_15m = 15
    private val TIMER_30m = 30
    private val TIMER_60m = 60
    private val TIMER_OFF = 0

    private lateinit var favoriteViewModel: FavoriteViewModel

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mBinding: ActivityPlayerMusicBinding? = null
        var songCurrent: Song? = null
        var indexSong = 0
        var songs: MutableList<Song> = mutableListOf()
        var musicService: MusicService? = null
        var indexTimer = 0
        var intentFrom = ""
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val indexTimerReceiver =
                    result.data?.getIntExtra("index_timer", INDEX_TIME_OFF) ?: INDEX_TIME_OFF
                indexTimer = indexTimerReceiver

                binding.apply {
                    textTimeLoop.apply {
                        if (indexTimer == INDEX_TIME_OFF) hide() else show()
                        text = when (indexTimer) {
                            INDEX_TIME_15m -> "15m"
                            INDEX_TIME_30m -> "30m"
                            INDEX_TIME_60m -> "60m"
                            else -> "Off"
                        }
                    }
                    iconTimes.isActivated = (indexTimer != INDEX_TIME_OFF)
                }

                if (indexTimer != INDEX_TIME_OFF) {
                    musicService!!.handleTimer(
                        when (indexTimer) {
                            INDEX_TIME_15m -> TIMER_15m
                            INDEX_TIME_30m -> TIMER_30m
                            INDEX_TIME_60m -> TIMER_60m
                            else -> TIMER_OFF
                        }
                    )
                } else {
                    musicService!!.cancelTimer()
                }
            }
        }

    override fun inflateViewBinding(): ActivityPlayerMusicBinding =
        ActivityPlayerMusicBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            mBinding = this
            isShuffle = intent.getBooleanExtra("is_shuffle", false)
            intentFrom = intent.getStringExtra("from") ?: ""

            songs = HomeActivity.songs
            if (isShuffle) songs.shuffle()
            indexSong = if (intentFrom == "nowPlayingFragment") {
                intent.getIntExtra("index_song", 0)
            } else {
                HomeActivity.indexSong
            }
            songCurrent = songs[indexSong]
            indexTimer = INDEX_TIME_OFF

            lifecycleOwner = this@PlayerMusicActivity
            textSongName.isActivated = true
            buttonPlay.isActivated = true
            progressSong.apply {
                progress = 0
                setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        if (p2) musicService?.exoPlayer?.seekTo(p1.toLong())
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) = Unit

                    override fun onStopTrackingTouch(p0: SeekBar?) = Unit
                })
            }

            val favoriteDAO = AppDatabase.getInstance(this@PlayerMusicActivity).favoriteDAO()
            val favoriteRepo = FavoriteRepo(favoriteDAO)
            val favoriteFactory = FavoriteFactory(favoriteRepo)
            favoriteViewModel =
                ViewModelProvider(
                    this@PlayerMusicActivity,
                    favoriteFactory
                )[FavoriteViewModel::class.java]

            isFavorite()
        }

        if (intentFrom != "nowPlayingFragment") {
            startService()
            handlePlayNewSong(songCurrent)
        } else {
            binding.apply {
                progressSong.apply {
                    max = musicService!!.exoPlayer!!.duration.toInt()
                    progress = musicService!!.exoPlayer!!.currentPosition.toInt()
                }
            }
        }
        showSongInformation(songCurrent)
    }

    override fun setUpListener() {
        binding.apply {
            iconBack.setClickAffect2 { finish() }

            buttonPlay.setClickAffect2 {
                val isPlaying = it?.isActivated ?: true
                it!!.isActivated = !isPlaying
                if (it.isActivated) handlePlayMusic() else handlePauseMusic()
            }

            buttonPrevious.setClickAffect2 { changeSong(isNextSong = false, isFromUser = true) }

            buttonNext.setClickAffect2 { changeSong(isNextSong = true, isFromUser = true) }

            buttonEqualizer.setClickAffect2 {
                val intent = Intent(this@PlayerMusicActivity, EqualizerActivity::class.java)
                startActivity(intent)
            }

            buttonLoop.setClickAffect2 {
                iconLoop.isActivated = !iconLoop.isActivated
            }

            buttonTimer.setClickAffect2 {
                val intent = Intent(this@PlayerMusicActivity, TimerActivity::class.java)
                intent.putExtra("index_timer", indexTimer)
                resultLauncher.launch(intent)
            }

            buttonShare.setClickAffect2 {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(songCurrent!!.path))
                }

                startActivity(Intent.createChooser(shareIntent, "Sharing Music File By VirgoMusic"))
            }

            iconFavorite.setClickAffect2 {
                var alreadyExitsInFavorite = false
                var indexFound = 0
                val listTemp = favoriteViewModel.listFavorite.value ?: mutableListOf()
                for (i in 0 until listTemp.size) {
                    alreadyExitsInFavorite =
                        (listTemp[i].idSong == songCurrent!!.id)
                    if (alreadyExitsInFavorite) {
                        indexFound = i
                        break
                    }
                }
                if (alreadyExitsInFavorite) {
                    // remove
                    favoriteViewModel.removeFavorite(listTemp[indexFound])
                    listTemp.remove(listTemp[indexFound])
                    binding.iconFavorite.isActivated = false
                } else {
                    // add
                    favoriteViewModel.insertFavorite(
                        FavoriteSong(
                            id = 0,
                            idSong = songCurrent!!.id
                        )
                    )
                    binding.iconFavorite.isActivated = true
                    listTemp.add(
                        if (listTemp.size - 1 < 0) 0 else listTemp.size - 1,
                        FavoriteSong(id = 0, idSong = songCurrent!!.id)
                    )
                }
            }
        }
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MusicBinder
        musicService = binder.getCurrentService()
        if (musicService!!.exoPlayer == null) {
            musicService!!.exoPlayer = ExoPlayer.Builder(this).build()
        }
        handlePlayNewSong(songCurrent)
        musicService!!.apply {
            startProgress()
            val isTimer = indexTimer != INDEX_TIME_OFF
            if (isTimer) handleTimer(indexTimer) else cancelTimer()
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun showSongInformation(song: Song?) {
        if (song == null) return
        binding.apply {
            textTimeEnd.text = song.formatDuration(song.duration)
            textSongName.text = song.title
        }

        NowPlayingFragment.mBinding.apply {
            song.apply {
                textSongName.text = title
                textAuthorName.text = artist
                buttonPlay.isActivated =
                    musicService?.exoPlayer?.isPlaying ?: true
            }
        }
    }

    private fun handlePlayNewSong(song: Song?) {
        actionWithTryCatch(
            MessageError.errorPlaySound,
            {
                // reset exo player
                musicService!!.exoPlayer?.apply {
                    stop()
                    release()
                }
                musicService!!.exoPlayer = null

                // handle play new song
                val mediaItem = MediaItem.fromUri(song?.path ?: "")
                musicService!!.exoPlayer = ExoPlayer.Builder(this@PlayerMusicActivity).build()
                musicService!!.apply {
                    exoPlayer!!.apply {
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
                                    changeSong(isNextSong = true, isFromUser = false)
                                }
                            }
                        })
                    }

                    // change notification ui
                    showNotification(R.drawable.icon_pause)
                    val isTimer = indexTimer != INDEX_TIME_OFF
                    if (isTimer) handleTimer(indexTimer) else cancelTimer()
                }

                // change seekbar
                binding.apply {
                    progressSong.apply {
                        progress = 0
                        max = musicService!!.exoPlayer!!.duration.toInt()
                    }

                    buttonPlay.isActivated = true
                }
            },
            { finish() }
        )
    }

    private fun handlePauseMusic() {
        actionWithTryCatch(
            MessageError.errorPauseSound,
            {
                musicService!!.apply {
                    exoPlayer!!.pause()
                    showNotification(R.drawable.icon_play)
                }

                NowPlayingFragment.mBinding.buttonPlay.isActivated = false
            },
            { finish() }
        )
    }

    private fun handlePlayMusic() {
        actionWithTryCatch(
            MessageError.errorPlaySound,
            {
                musicService!!.apply {
                    exoPlayer!!.play()
                    showNotification(R.drawable.icon_pause)
                }

                NowPlayingFragment.mBinding.buttonPlay.isActivated = true
            },
            { finish() }
        )
    }

    private fun changeSong(isNextSong: Boolean, isFromUser: Boolean) {
        // isFromUser là biến kiểm tra xem là bài hát tự hết hay là người dùng nhấn chuyển bài

        if (isFromUser) {
            indexSong = if (isNextSong) {
                val indexTemp = ++indexSong
                val isOverListSizeMax = indexTemp >= songs.size
                if (isOverListSizeMax) 0 else indexTemp
            } else {
                val indexTemp = --indexSong
                val isLowerListSizeMin = indexTemp <= -1
                if (isLowerListSizeMin) songs.size - 1 else indexTemp
            }
        } else {
            val isLoop = binding.iconLoop.isActivated
            indexSong = if (isLoop) {
                indexSong
            } else {
                if (isNextSong) {
                    val indexTemp = ++indexSong
                    val isOverListSizeMax = indexTemp >= songs.size
                    if (isOverListSizeMax) 0 else indexTemp
                } else {
                    val indexTemp = --indexSong
                    val isLowerListSizeMin = indexTemp <= -1
                    if (isLowerListSizeMin) songs.size - 1 else indexTemp
                }
            }
        }
        songCurrent = songs[indexSong]
        handlePlayNewSong(songCurrent)
        showSongInformation(songCurrent)
        isFavorite()
    }

    private fun isFavorite() {
        var alreadyExitsInFavorite = false
        val listTemp =
            AppDatabase.getInstance(this).favoriteDAO().getAllFavoriteSongMainThread()
        for (i in 0 until listTemp.size) {
            alreadyExitsInFavorite =
                (listTemp[i].idSong == songCurrent!!.id)
            if (alreadyExitsInFavorite) break
        }
        binding.iconFavorite.isActivated = alreadyExitsInFavorite
    }
}