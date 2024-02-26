package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.core.view.GravityCompat
import com.google.gson.Gson
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityMainBinding
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv.SongVerticalAdapter
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.service.MusicService
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.showToast
import java.io.File

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        lateinit var songs: MutableList<Song>
    }

    override fun inflateViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        songs = mutableListOf()
        if (isPermissionDenied()) {
            startActivity(Intent(this, PermissionActivity::class.java))
            return
        } else {
            getAllAudio()
        }
        binding.apply {
            lifecycleOwner = this@MainActivity

            rcv.apply {
                adapter = SongVerticalAdapter().apply {
                    setNewInstance(songs)

                    setOnItemClickListener { _, _, position ->
                        goToPlayerMusicScreen(false, position)
                    }
                }
                setItemViewCacheSize(13)
                setHasFixedSize(true)
            }

            textTotalSong.text = "${resources.getString(R.string.total_songs_)} ${songs.size}"
        }
    }

    override fun setUpListener() {
        binding.apply {
            buttonShuffle.setClickAffect2 {
                if (songs.isNotEmpty()) {
                    val indexSongDefault = 0
                    goToPlayerMusicScreen(true, indexSongDefault)
                } else { showToast(resources.getString(R.string.message_null_list_songs)) }
            }

            buttonFavorite.setClickAffect2 {
                val intent = Intent(this@MainActivity, FavoritesActivity::class.java)
                startActivity(intent)
            }

            buttonPlayList.setClickAffect2 {
                val intent = Intent(this@MainActivity, PlayListActivity::class.java)
                startActivity(intent)
            }

            iconNavigation.setClickAffect2 { drawerLayout.openDrawer(GravityCompat.START) }

            iconBack.setClickAffect2 { drawerLayout.closeDrawer(GravityCompat.START) }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionDined()
    }

    @SuppressLint("Range")
    private fun getAllAudio() {
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val projection = arrayOf(
            Media._ID,
            Media.TITLE,
            Media.ALBUM,
            Media.ARTIST,
            Media.DURATION,
            Media.DATE_ADDED,
            Media.DATA,
            Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${Media.DATE_ADDED} DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleCursor = cursor.getString(cursor.getColumnIndex(Media.TITLE))
                    val idCursor = cursor.getString(cursor.getColumnIndex(Media._ID))
                    val albumCursor = cursor.getString(cursor.getColumnIndex(Media.ALBUM))
                    val artistCursor = cursor.getString(cursor.getColumnIndex(Media.ARTIST))
                    val pathCursor = cursor.getString(cursor.getColumnIndex(Media.DATA))
                    val durationCursor = cursor.getLong(cursor.getColumnIndex(Media.DURATION))
                    val albumIdCursor = cursor.getString(cursor.getColumnIndex(Media.ALBUM_ID))

                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumIdCursor).toString()
                    val song = Song(
                        id = idCursor,
                        title = titleCursor,
                        album = albumCursor,
                        artist = artistCursor,
                        duration = durationCursor,
                        path = pathCursor,
                        artUri = artUri
                    )
                    val file = File(song.path)
                    if (file.exists()) songs.add(song)
                } while(cursor.moveToNext())
                cursor.close()
            }
        }
    }

    private fun goToPlayerMusicScreen(isShuffle: Boolean, positionSongInList: Int) {
        val intent = Intent(this@MainActivity, PlayerActivity::class.java)
        intent.putExtra("current_index_song", positionSongInList)
        intent.putExtra("is_shuffle", isShuffle)
        startActivity(intent)
    }
}