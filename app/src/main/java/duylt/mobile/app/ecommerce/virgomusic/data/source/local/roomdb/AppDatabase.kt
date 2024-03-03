package duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.roomdb.daos.FavoriteSongDAO
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong

@Database(entities = [FavoriteSong::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteDAO(): FavoriteSongDAO

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase.db"
                )
                    .allowMainThreadQueries()
                    .build()
            }

            return instance!!
        }
    }
}