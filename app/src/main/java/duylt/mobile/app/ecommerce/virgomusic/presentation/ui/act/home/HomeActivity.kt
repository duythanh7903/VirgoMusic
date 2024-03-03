package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityHomeBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.SongVerticalAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.factory.HomeFactory
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.PermissionActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.language.LanguageActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel.HomeViewModel
import duylt.mobile.app.ecommerce.virgomusic.utils.hide
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.show

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var songAdapter: SongVerticalAdapter

    companion object {
        lateinit var songs: MutableList<Song>
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var isPermissionDenied: Boolean = true
        var indexSong: Int = 0
    }

    override fun inflateViewBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            context = this@HomeActivity
            isPermissionDenied = isPermissionDenied()

            val viewModelFactory = HomeFactory()
            viewModel = ViewModelProvider(this@HomeActivity, viewModelFactory)[HomeViewModel::class.java]
            songs = mutableListOf()
            songAdapter = SongVerticalAdapter().apply {
                setNewInstance(mutableListOf())

                setOnItemClickListener { _, _, position ->
                    indexSong = position
                    startPlayerActivity(false)
                }
            }

            textTotalSong.text = "${resources.getString(R.string.total_songs_)}: ${songs.size}"
            lifecycleOwner = this@HomeActivity
            rcv.apply {
                adapter = songAdapter
                this.setItemViewCacheSize(15)
            }
        }
        observer()
    }

    override fun setUpListener() {
        binding.apply {
            buttonShuffle.setClickAffect2 {
                indexSong = 0
                startPlayerActivity(true)
            }

            buttonFavorite.setClickAffect2 {
                val intent = Intent(this@HomeActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }

            iconNavigation.setClickAffect2 {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }

            iconBack.setClickAffect2 {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            buttonLanguage.setClickAffect2 {
                val intent = Intent(this@HomeActivity, LanguageActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionDenied()) {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }

        songs.clear()
        viewModel.getAllSongFromDevice(this, isPermissionDenied)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observer() {
        viewModel.apply {
            isLoading.observe(this@HomeActivity) {
                binding.apply {
                    if (it) {
                        rcv.hide()
                        progressBar.show()
                    } else {
                        rcv.show()
                        progressBar.hide()
                    }
                }
            }

            songs.observe(this@HomeActivity) {
                HomeActivity.songs.addAll(it)
                songAdapter.apply {
                    setNewInstance(it)
                    notifyDataSetChanged()
                }
                binding.textTotalSong.text = "${resources.getString(R.string.total_songs_)} ${it.size}"
            }
        }
    }

    private fun startPlayerActivity(isShuffle: Boolean) {
        val intent = Intent(this@HomeActivity, PlayerMusicActivity::class.java)
        intent.putExtra("is_shuffle", isShuffle)
        startActivity(intent)
    }
}