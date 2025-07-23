package kzs.th000.curioushub.data.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val username: String?,
    /** Timestamp of last time update access token in milliseconds. */
    val accessTokenUpdateTime: Long,
    val accessToken: String,
    val accessTokenExpireTime: Int,
    val refreshToken: String,
    val refreshTokenExpireTime: Int,
    val tokenType: String,
)
