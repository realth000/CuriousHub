package kzs.th000.curioushub.features.profile.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.handleErrorWith
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kzs.th000.curioushub.core.exceptions.AppException
import kzs.th000.curioushub.core.network.GitHubHttpClient
import kzs.th000.curioushub.data.database.dao.UserDao
import kzs.th000.curioushub.features.profile.events.CurrentUserProfileEvent
import kzs.th000.curioushub.features.profile.state.CurrentUserProfileState

class CurrentUserProfileViewModel(
    private val userDao: UserDao,
    private val initialState: CurrentUserProfileState,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    fun onEvent(event: CurrentUserProfileEvent) {
        Log.i("CurrentUserProfileViewModel", "start event $event")
        when (event) {
            CurrentUserProfileEvent.FetchProfile -> onFetchProfile()
            CurrentUserProfileEvent.MarkTokenInvalid -> TODO()
            CurrentUserProfileEvent.RefreshAccessToken -> TODO()
        }
    }

    private fun onFetchProfile() =
        viewModelScope.launch {
            val username = _state.value.username
            val uid = _state.value.uid
            either {
                    val currentUser = userDao.getUserByUid(uid)
                    ensure(currentUser != null) { raise(AppException.InvalidUserToken.NotFound) }
                    GitHubHttpClient.getCurrentUserInfo(currentUser.accessToken)
                        .handleErrorWith { it ->
                            if (
                                it is AppException.HttpRequestFailed &&
                                    it.statusCode == HttpStatusCode.Unauthorized.value
                            ) {
                                // Token expires.
                                Log.i("CurrentUserProfileViewModel", "refresh access token")
                                val validToken = refreshAccessToken(currentUser.refreshToken).bind()
                                val validUser =
                                    currentUser.copy(
                                        accessToken = validToken.accessToken,
                                        accessTokenExpireTime = validToken.accessTokenExpireTime,
                                        accessTokenUpdateTime = System.currentTimeMillis(),
                                        refreshToken = validToken.refreshToken,
                                        refreshTokenExpireTime = validToken.refreshTokenExpireTime,
                                    )
                                userDao.upsertUser(validUser)
                                GitHubHttpClient.getCurrentUserInfo(validUser.accessToken)
                            } else {
                                it.left()
                            }
                        }
                        .bind()
                }
                .onLeft { it -> _state.emit(CurrentUserProfileState.Failure(it, username, uid)) }
                .onRight { it -> _state.emit(CurrentUserProfileState.Success(it)) }
        }

    private suspend fun refreshAccessToken(refreshToken: String) =
        GitHubHttpClient.refreshAccessToken(refreshToken)
}
