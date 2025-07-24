package kzs.th000.curioushub.core.exceptions

import android.net.Uri

/**
 * All errors used in this app.
 *
 * @param message Optional error message for all types of errors.
 */
sealed class AppException(open val message: String? = null) {

    /**
     * Error occurred during sending http request.
     *
     * @param method The method of the request.
     * @param target Target url of the request.
     * @param statusCode The returned status code, may not present if failed during SSL handshake.
     */
    data class HttpRequestFailed(
        val method: String,
        val target: Uri,
        val statusCode: Int?,
        override val message: String? = null,
    ) : AppException()

    /**
     * Server responded error, probably some invalid request.
     *
     * @param error Server responded error message.
     */
    data class HttpServerRespondedAnError(val error: String) : AppException(message = error)

    /** Failed to serialize or deserialize models. */
    data class SerializationFailure(override val message: String?) : AppException()

    /** User token is invalid, maybe not found or expired. */
    sealed class InvalidUserToken : AppException() {
        /** Token not found. */
        object NotFound : InvalidUserToken()

        /** Token is expired. */
        object Expired : InvalidUserToken()
    }

    /**
     * Failed to do the first time login. At this time we do not know any user related information.
     */
    data class AuthLoginFailed(override val message: String?) : AppException()

    /**
     * Failed to retrieve the login state of a certain user.
     *
     * This exception does not including first time login for the user, but a user who has logged in
     * before.
     *
     * @param username Username failed to refresh login state.
     * @param uid User id.
     */
    data class AuthRefreshUserStateFailed(
        val username: String,
        val uid: Int,
        override val message: String? = null,
    ) : AppException()
}
