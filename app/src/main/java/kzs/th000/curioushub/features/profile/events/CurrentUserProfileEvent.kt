package kzs.th000.curioushub.features.profile.events

/** All operations available in current user's profile page. */
sealed interface CurrentUserProfileEvent {
    /** Fetch profile data of current user. */
    object FetchProfile : CurrentUserProfileEvent

    /**
     * Refresh user access token.
     *
     * Only trigger when access token is expired.
     */
    object RefreshAccessToken : CurrentUserProfileEvent

    /** Mark that all user tokens are invalid, requires a new login. */
    object MarkTokenInvalid : CurrentUserProfileEvent
}
