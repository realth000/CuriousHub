package kzs.th000.curioushub.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kzs.th000.curioushub.data.database.dao.UserDao
import kzs.th000.curioushub.data.database.tables.User

@Database(version = 1, entities = [User::class], exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}
