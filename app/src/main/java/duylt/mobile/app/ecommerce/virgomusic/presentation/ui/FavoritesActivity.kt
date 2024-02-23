package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityFavoritesBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity

class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>() {
    override fun inflateViewBinding(): ActivityFavoritesBinding =
        ActivityFavoritesBinding.inflate(layoutInflater)

    override fun initView() {  }

    override fun setUpListener() {
    }
}