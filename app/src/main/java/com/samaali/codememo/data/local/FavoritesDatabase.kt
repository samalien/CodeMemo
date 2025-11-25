package com.samaali.codememo.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Ton entité
@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val algorithmId: Int
)

// Ta base de données
@Database(entities = [Favorite::class], version = 1, exportSchema = false)

abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}

// Ton DAO
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<Favorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE algorithmId = :id")
    suspend fun deleteById(id: Int)
}