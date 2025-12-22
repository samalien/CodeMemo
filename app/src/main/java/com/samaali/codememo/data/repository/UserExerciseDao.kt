import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.samaali.codememo.data.model.UserExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface UserExerciseDao {
    @Query("SELECT * FROM user_exercise ORDER BY id DESC")
    fun getAll(): Flow<List<UserExercise>>

    @Insert
    suspend fun insert(exercise: UserExercise)

    // AJOUT : suppression par ID
    @Query("DELETE FROM user_exercise WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Optionnel : supprimer tout (pour debug)
    // @Query("DELETE FROM user_exercise")
    // suspend fun deleteAll()
}