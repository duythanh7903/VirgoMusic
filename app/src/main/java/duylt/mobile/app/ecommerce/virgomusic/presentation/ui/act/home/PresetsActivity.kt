package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import android.content.Intent
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPresetsBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Preset
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.PresetAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2


class PresetsActivity : BaseActivity<ActivityPresetsBinding>() {

    private var indexSelected = 0

    override fun inflateViewBinding(): ActivityPresetsBinding =
        ActivityPresetsBinding.inflate(layoutInflater)

    override fun initView() {

        val eqType = intent.getStringExtra("eq_type") ?: "Custom"
        if (eqType == "Custom" || eqType == "Normal") indexSelected = 0
        for (i in 1 until getAllPresets().size) {
            if (getAllPresets()[i].title == eqType) {
                indexSelected = i
                break
            }
        }

        binding.apply {
            rcvEqualizerType.apply {
                adapter = PresetAdapter().apply {
                    setNewInstance(getAllPresets())

                    setOnItemClickListener { _, _, position ->
                        indexSelected = position
                        this@PresetsActivity.indexSelected = position
                    }

                    indexSelected = this@PresetsActivity.indexSelected
                }
            }
        }
    }

    override fun setUpListener() {
        binding.apply {
            iconBack.setClickAffect2 {
                finishScreen(indexSelected)
            }

            iconDone.setClickAffect2 {
                finishScreen(indexSelected)
            }
        }
    }

    private fun getAllPresets(): MutableList<Preset> =
        mutableListOf(
            Preset(index = 0, title = "Normal"),
            Preset(index = 1, title = "Classical"),
            Preset(index = 2, title = "Dance"),
            Preset(index = 3, title = "Flat"),
            Preset(index = 4, title = "Folk"),
            Preset(index = 5, title = "Heavy Metal"),
            Preset(index = 6, title = "Hip Hop"),
            Preset(index = 7, title = "Jazz"),
            Preset(index = 8, title = "Pop"),
            Preset(index = 9, title = "Rock"),
        )

    private fun finishScreen(index: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra("index_equalizer_type", index)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}