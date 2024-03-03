package duylt.mobile.app.ecommerce.virgomusic.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FAVORITE_SONG")
data class FavoriteSong(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id: Long = 0,
    @ColumnInfo(name = "ID_SONG")
    var idSong: String = ""
) {
}