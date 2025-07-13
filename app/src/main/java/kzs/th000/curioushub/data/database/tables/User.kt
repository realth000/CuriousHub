package kzs.th000.curioushub.data.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val accessToken: String,
    val accessTokenExpireTime: Int,
    val refreshToken: String,
    val refreshTokenExpireTime: Int,
    val tokenType: String,
)
