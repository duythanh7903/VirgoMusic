package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivitySplashBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.language.LanguageActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    companion object {
        const val TIMER_DURATION = 3000L
        const val TIMER_COUNT_DOWN = 1000L
    }

    private var countDownTimer: CountDownTimer? = null
    private var countIndex = 0

    override fun inflateViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        setStatusBarGradiant()
        startCountDown()
    }

    override fun setUpListener() = Unit

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(TIMER_DURATION, TIMER_COUNT_DOWN) {
            override fun onTick(p0: Long) {
                countIndex++
                binding.progressBar.progress =
                    (countIndex * 100) / (TIMER_DURATION / TIMER_COUNT_DOWN).toInt()
            }

            override fun onFinish() {
                countIndex++
                binding.progressBar.progress = 100
                nextScreen()
            }
        }
        countDownTimer?.start()
    }

    private fun nextScreen() {
        val isFirstInstall = SharePrefUtils.firstInstall
        val intent = Intent(this,
            if (isFirstInstall) LanguageActivity::class.java else IntroActivity::class.java
        )
        if (isFirstInstall) intent.putExtra("from_home", true)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 500)
    }
}