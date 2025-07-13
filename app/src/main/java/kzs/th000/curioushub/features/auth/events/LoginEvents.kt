package kzs.th000.curioushub.features.auth.events

/** All events and steps in the login process. */
sealed interface LoginEvent {
    /**
     * User starts login as the start of the login process.
     *
     * @param redirectUri The uri as callback, wake up activity on state.
     * @param state Extra parameter to avoid replay attack.
     */
    data class RequestCode(val redirectUri: String, val state: String) : LoginEvent

    /**
     * Use the code to get user tokens.
     *
     * @param clientId GitHub client id.
     * @param clientSecret GitHub client secret.
     * @param code The code we get after [RequestCode].
     * @param redirectUri Another uri where to invoke the application again as callback.
     */
    data class RequestToken(
        val clientId: String,
        val clientSecret: String,
        val code: String,
        val redirectUri: String,
    ) : LoginEvent

    /**
     * Already get user tokens.
     *
     * @param accessToken Server responded access token.
     * @param accessTokenExpireTime Timestamp access token expires.
     * @param refreshToken Server responded refresh token.
     * @param refreshTokenExpireTime Timestamp refresh token expires.
     */
    data class GotToken(
        val accessToken: String,
        val accessTokenExpireTime: Int,
        val refreshToken: String,
        val refreshTokenExpireTime: Int,
        val tokenType: String,
    ) : LoginEvent
}
