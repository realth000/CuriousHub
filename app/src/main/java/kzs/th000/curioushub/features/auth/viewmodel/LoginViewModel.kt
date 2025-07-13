package kzs.th000.curioushub.features.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
            val token =
                GitHubHttpClient.getAuthToken(
                    clientId = event.clientId,
                    clientSecret = event.clientSecret,
                    code = event.code,
                    redirectUri = event.redirectUri,
                )

            Log.d(".MainActivity", ">>> request token from $token")

            if (token == null) {
                _state.emit(LoginState.Failure)
                Log.i("LoginViewModel", ">>> request token: null result")
                return@launch
            }

            // Fetch necessary user info to associate tokens with exact user.
            // After this step we can save credentials into storage.

            val userProfile = GitHubHttpClient.getCurrentUserInfo(token.accessToken)
            if (userProfile == null) {
                Log.e("LoginViewModel", "got token but user profile not found")
                return@launch
            }

            userDao.insertUser(
                User(
                    username = userProfile.name,
                    accessToken = token.accessToken,
                    accessTokenExpireTime = token.accessTokenExpireTime,
                    refreshToken = token.refreshToken,
                    refreshTokenExpireTime = token.refreshTokenExpireTime,
                    tokenType = token.tokenType,
                )
            )

            // Login success, update current user.

            try {
                _state.emit(
                    LoginState.Success(
                        username = userProfile.name,
                        accessToken = token.accessToken,
                        accessTokenExpireTime = token.accessTokenExpireTime,
                        refreshToken = token.refreshToken,
                        refreshTokenExpireTime = token.refreshTokenExpireTime,
                        tokenType = token.tokenType,
                    )
                )
                Log.i("LoginVideModel", ">>> SUCCESS: $token")
            } catch (e: SerializationException) {
                _state.emit(LoginState.Failure)
                Log.i("LoginVideModel", ">>> EXCEPTION: $e")
            }
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
