package kzs.th000.curioushub

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kzs.th000.curioushub.core.constants.AUTH_TOKEN_URI
import kzs.th000.curioushub.core.models.AppSettings
import kzs.th000.curioushub.data.database.AppDatabase
import kzs.th000.curioushub.features.auth.events.LoginEvent
import kzs.th000.curioushub.features.auth.pages.LoginPage
import kzs.th000.curioushub.features.auth.state.LoginState
import kzs.th000.curioushub.features.auth.view_model.LoginViewModel
import kzs.th000.curioushub.features.profile.pages.CurrentUserProfilePage
import kzs.th000.curioushub.features.profile.state.CurrentUserProfileState
import kzs.th000.curioushub.features.profile.view_model.CurrentUserProfileViewModel

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {

    // FIXME: DI
    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "main.db").build()
    }

    private val loginViewModel by
        viewModels<LoginViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(db.userDao, LoginState.Initial) as T
                    }
                }
            }
        )

    private val currentUid by lazy {
        applicationContext.dataStore.data.map { pref -> AppSettings.currentUid.getOrNull(pref) }
    }

    private val currentUsername by lazy {
        applicationContext.dataStore.data.map { pref ->
            AppSettings.currentUsername.getOrNull(pref)
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Log.i("onNewIntent", ">>> uri: ${intent.data}")
        val code = intent.data?.getQueryParameter("code")
        val state = intent.data?.getQueryParameter("state")
        if (code != null && state != null) {
            loginViewModel.onEvent(
                LoginEvent.RequestToken(
                    clientId = BuildConfig.CLIENT_ID,
                    clientSecret = BuildConfig.CLIENT_SECRET,
                    code = code,
                    redirectUri = AUTH_TOKEN_URI,
                )
            )
            return
        }

        // TODO: WHY THESE CODE NOT CALLED.
        // We specified auth_token redirect uri when acquire token.

        val accessToken = intent.data?.getQueryParameter("access_token")
        val accessTokenExpireTime = intent.data?.getQueryParameter("expires_in")?.toIntOrNull()
        val refreshToken = intent.data?.getQueryParameter("refresh_token")
        val refreshTokenExpireTime =
            intent.data?.getQueryParameter("refresh_token_expires_in")?.toIntOrNull()
        val tokenType = intent.data?.getQueryParameter("token_type")

        if (
            accessToken != null &&
                accessTokenExpireTime != null &&
                refreshToken != null &&
                refreshTokenExpireTime != null &&
                tokenType != null
        ) {
            loginViewModel.onEvent(
                LoginEvent.GotToken(
                    accessToken,
                    accessTokenExpireTime,
                    refreshToken,
                    refreshTokenExpireTime,
                    tokenType,
                )
            )
            return
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val (username, uid) =
            runBlocking(Dispatchers.Default) {
                // Update login state.
                val username = currentUsername.first()
                val uid = currentUid.first()
                Log.d("MainActivity::initCurrent", "current user: $username ($uid)")
                return@runBlocking Pair(username, uid)
            }

        val initialRoute =
            if (uid == null || username == null) {
                LoginPageRoute
            } else {
                CurrentUserProfilePageRoute(username = username, uid)
            }

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = initialRoute) {
                composable<LoginPageRoute> {
                    LoginPage(
                        loginViewModel,
                        onLaunchUrl = { targetUri ->
                            val browserIntent = Intent(Intent.ACTION_VIEW, targetUri)
                            browserIntent.flags = FLAG_ACTIVITY_NEW_TASK
                            applicationContext.startActivity(browserIntent)
                        },
                        onLoginSuccess = { username, uid ->
                            lifecycleScope.launch {
                                Log.d("UserLoginSuccess", "user login success: $username")
                                dataStore.edit { settings ->
                                    AppSettings.currentUid.update(settings, uid)
                                    AppSettings.currentUsername.update(settings, username)
                                }
                            }
                            navController.navigate(CurrentUserProfilePageRoute(username, uid))
                        },
                    )
                }
                composable<CurrentUserProfilePageRoute> {
                    val routeArgs = it.toRoute<CurrentUserProfilePageRoute>()
                    CurrentUserProfilePage(
                        viewModel =
                            CurrentUserProfileViewModel(
                                db.userDao,
                                CurrentUserProfileState.Initial(
                                    username = routeArgs.username,
                                    uid = routeArgs.uid,
                                ),
                            )
                    )
                }

                // composable<LoginAuthTokenDeepLinkParam>(
                //     deepLinks =
                //         listOf(navDeepLink<LoginAuthTokenDeepLinkParam>(basePath =
                // AUTH_TOKEN_URI))
                // ) {
                //     val params = it.toRoute<LoginAuthTokenDeepLinkParam>()
                //     setContent {
                //         LoginPage(
                //             LoginViewModel(
                //                 db.userDao,
                //                 LoginState.Success(
                //                     accessToken = params.accessToken!!,
                //                     accessTokenExpireTime = params.accessTokenExpireTime!!,
                //                     refreshToken = params.refreshToken!!,
                //                     refreshTokenExpireTime = params.refreshTokenExpireTime!!,
                //                     tokenType = params.tokenType!!,
                //                 ),
                //             ),
                //             onLoginSuccess = {
                //                 /* On login success */
                //                 finishActivity(0)
                //             },
                //         )
                //     }
                // }

                // composable<LoginAuthCodeDeepLinkParam>(
                //     deepLinks =
                //         listOf(navDeepLink<LoginAuthCodeDeepLinkParam>(basePath = AUTH_CODE_URI))
                // ) {
                //     val params = it.toRoute<LoginAuthCodeDeepLinkParam>()
                //     LoginPage(
                //         LoginViewModel(
                //             db.userDao,
                //             LoginState.GotCode(code = params.code!!, state = params.state!!),
                //         ),
                //         onLoginSuccess = {
                //             /* On login success */
                //             finishActivity(0)
                //         },
                //     )
                // }
            }
        }
    }
}

@Serializable object LoginPageRoute

@Serializable data class CurrentUserProfilePageRoute(val username: String, val uid: Int)
