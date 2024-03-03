package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityTimerBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Timer
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.TimerAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2

class TimerActivity : BaseActivity<ActivityTimerBinding>() {

    private var indexTimer = 0

    override fun inflateViewBinding(): ActivityTimerBinding =
        ActivityTimerBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {

            indexTimer = intent.getIntExtra("index_timer", 0)

            rcv.adapter = TimerAdapter().apply {
                setNewInstance(getTimers())

                setOnItemClickListener { _, _, position ->
                    indexSelected = position
                    indexTimer = position
                }

                indexSelected = indexTimer
            }
        }
    }

    override fun setUpListener() {
        binding.apply {
            iconBack.setClickAffect2 { finishScreen(indexTimer) }

            iconDone.setClickAffect2 { finishScreen(indexTimer) }
        }
    }

    private fun getTimers() = mutableListOf(
        Timer(0, "Off", 0),
        Timer(1, "15 minutes", 15),
        Timer(2, "30 minutes", 30),
        Timer(3, "60 minutes", 60),
    )

    private fun finishScreen(index: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra("index_timer", index)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}