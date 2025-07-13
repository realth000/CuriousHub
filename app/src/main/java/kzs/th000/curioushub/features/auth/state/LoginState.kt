package kzs.th000.curioushub.features.auth.state

/** All possible states when doing login process. */
sealed interface LoginState {
    /** The initial state. */
    object Initial : LoginState

    /** Fetching code. */
    object LoadingCode : LoginState

    /**
     * Successfully get code.
     *
     * @param code Currently holding code to fetch token.
     * @param state The state parameter used to get token.
     */
    data class GotCode(val code: String, val state: String) : LoginState

    /** Fetching user token. */
    object LoadingToken : LoginState

    /**
     * Successfully got user tokens.
     *
     * Docs:
     * * https://docs.github.com/apps/creating-github-apps/authenticating-with-a-github-app/generating-a-user-access-token-for-a-github-app
     *
     * @param username Username of current user.
     * @param accessToken Server responded access token.
     * @param accessTokenExpireTime Timestamp access token expires.
     * @param refreshToken Server responded refresh token.
     * @param refreshTokenExpireTime Timestamp refresh token expires.
     */
    data class Success(
        val username: String,
        val accessToken: String,
        val accessTokenExpireTime: Int,
        val refreshToken: String,
        val refreshTokenExpireTime: Int,
        val tokenType: String,
    ) : LoginState

    // TODO: Record error message.
    /**
     * Failed to login.
     *
     * Currently we do not store any error info or error message.
     */
    object Failure : LoginState
}
