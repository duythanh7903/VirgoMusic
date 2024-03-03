package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb.AppDatabase
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityFavoriteBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.domain.repository.FavoriteRepo
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.SongVerticalAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.factory.FavoriteFactory
import duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel.FavoriteViewModel
import duylt.mobile.app.ecommerce.virgomusic.utils.hide
import duylt.mobile.app.ecommerce.virgomusic.utils.show
import duylt.mobile.app.ecommerce.virgomusic.utils.showToast

class FavoriteActivity : BaseActivity<ActivityFavoriteBinding>() {

    private lateinit var vm: FavoriteViewModel
    private lateinit var songsFavorite: MutableList<FavoriteSong>
    private lateinit var songs: MutableList<Song>
    private lateinit var songAdapter: SongVerticalAdapter

    override fun inflateViewBinding(): ActivityFavoriteBinding =
        ActivityFavoriteBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            val dao = AppDatabase.getInstance(this@FavoriteActivity).favoriteDAO()
            val repo = FavoriteRepo(dao)
            val factory = FavoriteFactory(repo)
            vm = ViewModelProvider(this@FavoriteActivity, factory)[FavoriteViewModel::class.java]

            songsFavorite = mutableListOf()
            songs = mutableListOf()
            songAdapter = SongVerticalAdapter().apply {
                setNewInstance(songs)
                setOnItemClickListener { _, _, position ->
                    showToast(songs[position].toString())
                }
            }

            rcv.adapter = songAdapter
        }

        observer()
    }

    override fun setUpListener() {
        binding.apply {
            iconBack.setOnClickListener { finish() }
        }
    }

    private fun observer() {
        vm.apply {
            isLoading.observe(this@FavoriteActivity) {
                if (it) {
                    binding.progressBar.show()
                    binding.rcv.hide()
                } else {
                    binding.progressBar.hide()
                    binding.rcv.show()
                }
            }

            listFavorite.observe(this@FavoriteActivity) {
                songsFavorite = it
                for (i in 0 until  songsFavorite.size) {
                    for (j in 0 until HomeActivity.songs.size) {
                        if (songsFavorite[i].idSong == HomeActivity.songs[j].id) {
                            songs.add(HomeActivity.songs[j])
                            break
                        }
                    }
                }

                songAdapter.notifyDataSetChanged()
            }
        }
    }
}