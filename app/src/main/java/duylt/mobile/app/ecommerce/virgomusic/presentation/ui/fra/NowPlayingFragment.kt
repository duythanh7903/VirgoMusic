package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.fra

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.FragmentNowPlayingBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseFragment
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.PlayerMusicActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.hide
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.show

class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding>() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mBinding: FragmentNowPlayingBinding
    }

    override fun inflateViewBinding(): FragmentNowPlayingBinding =
        FragmentNowPlayingBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            mBinding = binding

            root.hide()
        }
    }

    override fun setUpListener() {
        binding.apply {
            buttonPlay.setClickAffect2 {
                val isMusicPlaying = PlayerMusicActivity.musicService!!.exoPlayer?.isPlaying ?: false
                if (isMusicPlaying) pauseMusic() else playMusic()
            }

            root.setClickAffect2 {
                val intent = Intent(requireActivity(), PlayerMusicActivity::class.java)
                intent.putExtra("index_song", PlayerMusicActivity.indexSong)
                intent.putExtra("from", "nowPlayingFragment")
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val serviceMusicIsRunning = PlayerMusicActivity.musicService != null
        binding.apply {
            if (PlayerMusicActivity.musicService != null) {
                val isMusicPlaying = PlayerMusicActivity.musicService!!.exoPlayer?.isPlaying ?: false

                root.apply {
                    if (serviceMusicIsRunning) show() else hide()
                }
                textSongName.text = PlayerMusicActivity.songCurrent?.title ?: ""
                textAuthorName.text = PlayerMusicActivity.songCurrent?.artist ?: ""
                buttonPlay.isActivated = isMusicPlaying
            }
        }
    }

    private fun playMusic() {
        binding.buttonPlay.isActivated = true
        PlayerMusicActivity.apply {
            musicService!!.apply {
                exoPlayer!!.play()
                showNotification(R.drawable.icon_pause)
            }
            mBinding!!.apply {
                buttonPlay.isActivated = true
            }
        }
    }

    private fun pauseMusic() {
        binding.buttonPlay.isActivated = false
        PlayerMusicActivity.apply {
            musicService!!.apply {
                exoPlayer!!.pause()
                showNotification(R.drawable.icon_play)
            }
            mBinding!!.apply {
                buttonPlay.isActivated = true
            }
        }
    }
}