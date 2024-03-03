package duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb.daos

import androidx.room.*
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong

@Dao
interface FavoriteSongDAO {
    @Insert(entity = FavoriteSong::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(s: FavoriteSong)

    @Update(entity = FavoriteSong::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(s: FavoriteSong)

    @Delete(entity = FavoriteSong::class)
    suspend fun delete(s: FavoriteSong)

    @Query("SELECT * FROM FAVORITE_SONG")
    suspend fun getAllFavoriteSong(): MutableList<FavoriteSong>

    @Query("SELECT * FROM FAVORITE_SONG")
    fun getAllFavoriteSongMainThread(): MutableList<FavoriteSong>
}