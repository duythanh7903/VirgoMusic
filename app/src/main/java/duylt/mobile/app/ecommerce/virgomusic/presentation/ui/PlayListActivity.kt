package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPlayListBinding
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPlayerBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity

class PlayListActivity : BaseActivity<ActivityPlayListBinding>() {
    override fun inflateViewBinding(): ActivityPlayListBinding =
        ActivityPlayListBinding.inflate(layoutInflater)

    override fun initView() {
    }

    override fun setUpListener() {
    }
}