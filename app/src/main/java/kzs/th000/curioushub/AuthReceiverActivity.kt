package kzs.th000.curioushub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kzs.th000.curioushub.ui.theme.CuriousHubTheme

class AuthReceiverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.data

        setContent {
            CuriousHubTheme {
                Scaffold { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Text(text = "Receive auth result")
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(text = "URL: $url")
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                // TODO: Use code to exchange access token and refresh token.
                                //
                                // Next steps:
                                //
                                // 1. Post request to get the access token and refresh token.
                                // 2. Store access token and refresh token.
                            },
                        ) {
                            Text(text = "Continue login")
                        }
                    }
                }
            }
        }
    }
}
