package kzs.th000.curioushub.features.auth.pages

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kzs.th000.curioushub.BuildConfig
import kzs.th000.curioushub.core.constants.AUTH_CODE_URI
import kzs.th000.curioushub.core.constants.AUTH_TOKEN_URI
import kzs.th000.curioushub.core.network.AppHttpClient
import kzs.th000.curioushub.core.network.AppUriParam
import kzs.th000.curioushub.features.auth.events.LoginEvent
import kzs.th000.curioushub.features.auth.state.LoginState
import kzs.th000.curioushub.features.auth.view_model.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    loginViewModel: LoginViewModel,
    onLoginSuccess: (username: String, uid: Int) -> Unit,
    onLaunchUrl: ((Uri) -> Unit)? = null,
) {
    val state by loginViewModel.state.collectAsState()

    val processing =
        when (state) {
            LoginState.Initial -> false
            LoginState.LoadingCode,
            LoginState.LoadingToken,
            is LoginState.GotCode -> true
            is LoginState.Success -> false
            is LoginState.Failure -> false
        }

    val tipText =
        when (state) {
            LoginState.Initial -> "Ready to login GitHub?"
            LoginState.LoadingCode,
            LoginState.LoadingToken,
            is LoginState.GotCode -> "Processing..."
            is LoginState.Success -> "Login success"
            is LoginState.Failure -> "Login failed"
        }

    LaunchedEffect(state) {
        when (state) {
            LoginState.Initial,
            LoginState.LoadingToken -> Unit
            is LoginState.Failure ->
                Log.e("LoginPage", "failed to login: ${(state as LoginState.Failure).error}")

            is LoginState.GotCode -> {
                loginViewModel.onEvent(
                    LoginEvent.RequestToken(
                        clientId = BuildConfig.CLIENT_ID,
                        clientSecret = BuildConfig.CLIENT_SECRET,
                        code = (state as LoginState.GotCode).code,
                        redirectUri = AUTH_TOKEN_URI,
                    )
                )
            }
            LoginState.LoadingCode -> {
                val targetUrl =
                    AppHttpClient.buildGhTarget(
                        listOf("login", "oauth", "authorize"),
                        listOf(
                            AppUriParam(key = "client_id", value = BuildConfig.CLIENT_ID),
                            AppUriParam(key = "state", value = "hello_curious_hub_debug"),
                            AppUriParam(key = "redirect_uri", value = AUTH_CODE_URI),
                        ),
                    )
                onLaunchUrl!!.invoke(targetUrl)
            }
            is LoginState.Success -> {
                val successState = state as LoginState.Success
                onLoginSuccess.invoke(successState.username, successState.uid)
            }
        }
    }

    val buttonCallback = {
        when (state) {
            LoginState.Initial,
            is LoginState.Failure ->
                loginViewModel.onEvent(
                    LoginEvent.RequestCode(redirectUri = AUTH_CODE_URI, state = "debug_test")
                )
            is LoginState.GotCode,
            LoginState.LoadingCode,
            is LoginState.Success,
            LoginState.LoadingToken -> {
                /* Do nothing */
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Login GitHub") }) }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(tipText)
            Button(enabled = !processing, onClick = buttonCallback) {
                when (state) {
                    LoginState.Initial -> {
                        Text("Login")
                    }
                    LoginState.LoadingCode,
                    LoginState.LoadingToken,
                    is LoginState.GotCode -> {
                        CircularProgressIndicator()
                    }
                    is LoginState.Success -> {
                        Text("continue")
                    }
                    is LoginState.Failure -> {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
