package kzs.th000.curioushub.features.profile.state

import kzs.th000.curioushub.core.exceptions.AppException
import kzs.th000.curioushub.core.models.UserProfileModel

/**
 * All possible state of current user profile page.
 *
 * @param username Current username.
 * @param uid Current user id.
 */
sealed class CurrentUserProfileState(open val username: String? = "<unknown>", open val uid: Int) {
    /** The initial state. */
    data class Initial(override val username: String?, override val uid: Int) :
        CurrentUserProfileState(username, uid)

    /** Loading profile data. */
    data class Loading(override val username: String?, override val uid: Int) :
        CurrentUserProfileState(username, uid)

    /**
     * Failed to load data.
     *
     * @param error Occurred exception.
     */
    data class Failure(
        val error: AppException,
        override val username: String?,
        override val uid: Int,
    ) : CurrentUserProfileState(username, uid)

    /**
     * Successfully loaded user profile.
     *
     * @param userProfile Model of user profile.
     */
    data class Success(val userProfile: UserProfileModel) :
        CurrentUserProfileState(userProfile.login, userProfile.id)
}
