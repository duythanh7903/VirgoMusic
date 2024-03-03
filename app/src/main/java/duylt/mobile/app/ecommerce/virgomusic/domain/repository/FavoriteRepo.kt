package duylt.mobile.app.ecommerce.virgomusic.domain.repository

import duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb.daos.FavoriteSongDAO
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong

class FavoriteRepo(
    private val dao: FavoriteSongDAO
) {

    suspend fun getAllSong(): MutableList<FavoriteSong> = dao.getAllFavoriteSong()

    suspend fun insert(s: FavoriteSong) {
        dao.insert(s)
    }

    suspend fun update(s: FavoriteSong) {
        dao.update(s)
    }

    suspend fun delete(s: FavoriteSong) {
        dao.delete(s)
    }

}