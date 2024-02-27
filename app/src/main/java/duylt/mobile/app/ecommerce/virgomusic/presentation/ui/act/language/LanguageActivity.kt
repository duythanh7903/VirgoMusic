package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.language

import android.content.Intent
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityLanguageBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Language
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.LanguageAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.intro.IntroActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.hide
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.show

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    private var countriesCode = SharePrefUtils.language
    private var isFromHome = false

    override fun inflateViewBinding(): ActivityLanguageBinding =
        ActivityLanguageBinding.inflate(layoutInflater)

    override fun initView() {
        isFromHome = intent.getBooleanExtra("from_home", false)
        binding.apply {
            lifecycleOwner = this@LanguageActivity
            rvLanguage.apply {
                adapter = LanguageAdapter().apply {
                    setNewInstance(getListCountries())

                    setOnItemClickListener { _, _, position->
                        val code = this.data[position].code
                        this.selectedLang = code
                        countriesCode = code
                    }
                }
            }
            if (isFromHome) ivBack.hide() else ivBack.show()
        }
    }

    override fun setUpListener() {
        binding.apply {
            ivBack.setClickAffect2 { finish() }

            ivTick.setClickAffect2 {
                SharePrefUtils.language = countriesCode
                if (isFromHome) SharePrefUtils.firstInstall = false
                val intent = Intent(this@LanguageActivity, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getListCountries(): MutableList<Language> =
        mutableListOf(
            Language(R.drawable.ic_english, "English", "en"),
            Language(R.drawable.ic_france, "French", "fr"),
            Language(R.drawable.ic_india, "Hindi", "hi"),
            Language(R.drawable.ic_portugal, "Portuguese", "pt"),
            Language(R.drawable.ic_spanish, "Spanish", "es"),
        )
}