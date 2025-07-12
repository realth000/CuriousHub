package kzs.th000.curioushub

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kzs.th000.curioushub.ui.theme.CuriousHubTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CuriousHubTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Hello") },
                        )
                    },
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onStartAuth = {
                            val uriBuilder = Uri.Builder()
                            val authUrl =
                                uriBuilder
                                    .scheme("https")
                                    .authority("github.com")
                                    .appendPath("login")
                                    .appendPath("oauth")
                                    .appendPath("authorize")
                                    .appendQueryParameter(
                                        "client_id",
                                        BuildConfig.CLIENT_ID,
                                    ).appendQueryParameter(
                                        "state",
                                        "hello_curious_hub_debug",
                                    ).appendQueryParameter(
                                        "redirect_uri",
                                        "curioushub://authed",
                                    ).build()

                            val browseIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    authUrl,
                                )
                            browseIntent.flags = FLAG_ACTIVITY_NEW_TASK

                            applicationContext.startActivity(browseIntent)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    onStartAuth: () -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(12.dp),
    ) {
        item(key = "hello") {
            Text(
                text = "Hello $name!",
                modifier = modifier,
            )
        }

        item(key = "BUILD_TYPE") {
            Text(
                text = BuildConfig.BUILD_TYPE,
            )
        }

        item(key = "VERSION_NAME") {
            Text(
                text = BuildConfig.VERSION_NAME,
            )
        }

        item(key = "VERSION_CODE") {
            Text(
                text = "${BuildConfig.VERSION_CODE}",
            )
        }

        item(key = "APP_ID") {
            Text(
                text = BuildConfig.APPLICATION_ID,
            )
        }

        item(key = "GH_CLIENT_SECRET") {
            Text(
                text = BuildConfig.CLIENT_ID,
            )
        }

        item(key = "GH_CLIENT_ID") {
            Text(
                text = BuildConfig.CLIENT_SECRET,
            )
        }

        item(key = "spacer1") {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item(key = "BUTTON_TO_AUTH") {
            Button(
                onClick = onStartAuth,
            ) {
                Text("Login GitHub")
            }
        }
    }
}
