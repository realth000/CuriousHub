package kzs.th000.curioushub.features.auth.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kzs.th000.curioushub.core.exceptions.AppException
import kzs.th000.curioushub.core.network.GitHubHttpClient
import kzs.th000.curioushub.data.database.dao.UserDao
import kzs.th000.curioushub.data.database.tables.User
import kzs.th000.curioushub.features.auth.events.LoginEvent
import kzs.th000.curioushub.features.auth.state.LoginState

/** The view model of login page. */
class LoginViewModel(private val userDao: UserDao, private val initialState: LoginState) :
    ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    fun onEvent(event: LoginEvent) {
        Log.i("LoginViewModel", "start event $event")
        when (event) {
            is LoginEvent.RequestCode -> onRequestCode()
            is LoginEvent.RequestToken -> onRequestToken(event)
            is LoginEvent.GotToken -> onGotToken(event)
        }
    }

    /**
     * Get code from server.
     *
     * This is the first step when login.
     *
     * Getting code is working in external browser, just change the state.
     */
    private fun onRequestCode() = viewModelScope.launch { _state.emit(LoginState.LoadingCode) }

    /**
     * Get user tokens from server.
     *
     * This is the first step when login.
     *
     * The last request send to server.
     */
    private fun onRequestToken(event: LoginEvent.RequestToken) =
        viewModelScope.launch {
            either<AppException, Unit> {
                    val token =
                        GitHubHttpClient.getAuthToken(
                                clientId = event.clientId,
                                clientSecret = event.clientSecret,
                                code = event.code,
                                redirectUri = event.redirectUri,
                            )
                            .bind()

                    // Fetch necessary user info to associate tokens with exact user.
                    // After this step we can save credentials into storage.

                    val userProfile = GitHubHttpClient.getCurrentUserInfo(token.accessToken).bind()

                    userDao.upsertUser(
                        User(
                            id = userProfile.id,
                            accessTokenUpdateTime = System.currentTimeMillis(),
                            username = userProfile.login,
                            accessToken = token.accessToken,
                            accessTokenExpireTime = token.accessTokenExpireTime,
                            refreshToken = token.refreshToken,
                            refreshTokenExpireTime = token.refreshTokenExpireTime,
                            tokenType = token.tokenType,
                        )
                    )

                    // Login success, update current user.
                    _state.emit(
                        LoginState.Success(
                            username = userProfile.login,
                            uid = userProfile.id,
                            accessToken = token.accessToken,
                            accessTokenExpireTime = token.accessTokenExpireTime,
                            refreshToken = token.refreshToken,
                            refreshTokenExpireTime = token.refreshTokenExpireTime,
                            tokenType = token.tokenType,
                        )
                    )
                }
                .onLeft { it -> _state.emit(LoginState.Failure(it)) }
        }

    private fun onGotToken(event: LoginEvent.GotToken) =
        viewModelScope.launch {
            // _state.emit(
            //     LoginState.Success(
            //         accessToken = event.accessToken,
            //         accessTokenExpireTime = event.accessTokenExpireTime,
            //         refreshToken = event.refreshToken,
            //         refreshTokenExpireTime = event.refreshTokenExpireTime,
            //         tokenType = event.tokenType,
            //     )
            // )
            // Log.i("LoginVideModel", ">>> SUCCESS onGotToken: $event")
        }
}
