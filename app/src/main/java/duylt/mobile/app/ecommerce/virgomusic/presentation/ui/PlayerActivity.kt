package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPlayerBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity

class PlayerActivity : BaseActivity<ActivityPlayerBinding>() {
    override fun inflateViewBinding(): ActivityPlayerBinding =
        ActivityPlayerBinding.inflate(layoutInflater)

    override fun initView() {
    }

    override fun setUpListener() {
    }
}