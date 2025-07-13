package kzs.th000.curioushub.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenSuccessModel(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val accessTokenExpireTime: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("refresh_token_expires_in") val refreshTokenExpireTime: Int,
    @SerialName("token_type") val tokenType: String,
)
