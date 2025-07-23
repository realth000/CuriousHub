package kzs.th000.curioushub.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kzs.th000.curioushub.data.database.tables.User

@Dao
interface UserDao {
    @Upsert suspend fun upsertUser(user: User)

    @Delete suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users") fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username==:username LIMIT 1")
    suspend fun getUserByName(username: String): User?

    @Query("SELECT * FROM users WHERE id==:uid LIMIT 1") suspend fun getUserByUid(uid: Int): User?
}
