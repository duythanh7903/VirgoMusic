package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import android.content.Intent
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityMainBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun inflateViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun initView() {
    }

    override fun setUpListener() {
        binding.apply {
            buttonShuffle.setClickAffect2 {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                startActivity(intent)
            }

            buttonFavorite.setClickAffect2 {
                val intent = Intent(this@MainActivity, FavoritesActivity::class.java)
                startActivity(intent)
            }

            buttonPlayList.setClickAffect2 {
                val intent = Intent(this@MainActivity, PlayListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}