package com.samaali.codememo.data.repository

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.samaali.codememo.data.model.UserExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface UserExerciseDao {
    // Récupère tous les exercices, triés du plus récent au plus ancien
    @Query("SELECT * FROM user_exercise ORDER BY id DESC")
    fun getAll(): Flow<List<UserExercise>>

    @Insert
    suspend fun insert(exercise: UserExercise)

    // NOUVEAU : suppression par ID
    @Query("DELETE FROM user_exercise WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Optionnel : supprimer tous les exercices (utile pour debug ou reset)
    @Query("DELETE FROM user_exercise")
    suspend fun deleteAll()
}

@Database(entities = [UserExercise::class], version = 1, exportSchema = false)
abstract class UserExerciseDatabase : RoomDatabase() {
    abstract fun dao(): UserExerciseDao
}

class UserExerciseRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,  // Meilleure pratique : utiliser applicationContext
        UserExerciseDatabase::class.java,
        "user_exercise.db"
    ).build()

    fun getAll(): Flow<List<UserExercise>> = db.dao().getAll()

    suspend fun insert(exercise: UserExercise) = db.dao().insert(exercise)

    // NOUVEAU : fonction de suppression
    suspend fun deleteById(id: Int) = db.dao().deleteById(id)

    // Optionnel : tout supprimer
    suspend fun deleteAll() = db.dao().deleteAll()
}