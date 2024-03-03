package duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel: ViewModel() {
    val songs = MutableLiveData<MutableList<Song>>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        getAllSongFromDevice(HomeActivity.context, HomeActivity.isPermissionDenied)
    }

    fun getAllSongFromDevice(context: Context, isPermissionDenied: Boolean) {
        viewModelScope.launch {
            if (isPermissionDenied) {
                isLoading.postValue(false)
                songs.postValue(mutableListOf())
            } else {
                withContext(Dispatchers.IO) {
                    isLoading.postValue(true)
                    handleGetAllSongsFromDevice(context)
                    isLoading.postValue(false)
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun handleGetAllSongsFromDevice(context: Context) {
        val listSong: MutableList<Song> = mutableListOf()
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC",
            null
        ) ?: return
        if (cursor.moveToFirst()) {
            do {
                val titleCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val idCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artistCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val pathCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val durationCursor = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val albumIdCursor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

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
                if (file.exists()) listSong.add(song)
            } while(cursor.moveToNext())
            cursor.close()
            songs.postValue(listSong)
        }
    }
}