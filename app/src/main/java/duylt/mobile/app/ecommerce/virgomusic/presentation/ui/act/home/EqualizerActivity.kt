package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import android.graphics.Color
import android.preference.PreferenceManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.gson.Gson
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityEqualizerBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.EqualizerSettings
import duylt.mobile.app.ecommerce.virgomusic.libs.equalizer.EqualizerFragment
import duylt.mobile.app.ecommerce.virgomusic.libs.equalizer.EqualizerModel
import duylt.mobile.app.ecommerce.virgomusic.libs.equalizer.Settings
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.service.MusicService
import duylt.mobile.app.ecommerce.virgomusic.utils.logger

class EqualizerActivity : BaseActivity<ActivityEqualizerBinding>() {

    companion object {
        const val PREF_KEY = "equalizer"

        var sessionId: Int = 0
    }

    override fun inflateViewBinding(): ActivityEqualizerBinding =
        ActivityEqualizerBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            loadEqualizerSettings()
            handleEnableEqualizer()

            PlayerMusicActivity.musicService!!.exoPlayer!!.addListener(object : Listener {
                @Deprecated("Deprecated in Java")
                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    @Player.State state: Int
                ) {
                    if (state == ExoPlayer.STATE_ENDED) {
                        handleEnableEqualizer()
                    }
                }
            })
        }
    }

    override fun setUpListener() {
        binding.apply {

        }
    }

    private fun loadEqualizerSettings() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val settings: EqualizerSettings = gson.fromJson(
            preferences.getString(PREF_KEY, "{}"),
            EqualizerSettings::class.java
        )
        val model = EqualizerModel()
        model.setBassStrength(settings.bassStrength)
        model.setPresetPos(settings.presetPos)
        model.setReverbPreset(settings.reverbPreset)
        model.setSeekbarpos(settings.seekbarpos)
        Settings.isEqualizerEnabled = false
        Settings.isEqualizerReloaded = true
        Settings.bassStrength = settings.bassStrength
        Settings.presetPos = settings.presetPos
        Settings.reverbPreset = settings.reverbPreset
        Settings.seekbarpos = settings.seekbarpos
        Settings.equalizerModel = model
    }

    fun handleEnableEqualizer() {
        sessionId = PlayerMusicActivity.musicService?.exoPlayer?.audioSessionId ?: 1
        logger("Check Session id: $sessionId")
        val equalizerFragment: EqualizerFragment = EqualizerFragment.newBuilder()
            .setAccentColor(Color.parseColor("#4caf50"))
            .setAudioSessionId(sessionId)
            .build()
        supportFragmentManager.beginTransaction()
            .replace(binding.eqFrame.id, equalizerFragment)
            .commit()
    }
}