package kzs.th000.curioushub.features.profile.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import kzs.th000.curioushub.features.profile.events.CurrentUserProfileEvent
import kzs.th000.curioushub.features.profile.state.CurrentUserProfileState
import kzs.th000.curioushub.features.profile.view_model.CurrentUserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentUserProfilePage(
    viewModel: CurrentUserProfileViewModel,
    onFetchProfileFailed: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is CurrentUserProfileState.Initial -> {
                // Verify token status by loading profile data.
                viewModel.onEvent(CurrentUserProfileEvent.FetchProfile)
            }
            is CurrentUserProfileState.Loading -> /*  Do nothing */ Unit
            is CurrentUserProfileState.Failure -> /*  Do nothing */ Unit
            is CurrentUserProfileState.Success -> /*  Do nothing */ Unit
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
            when (state) {
                is CurrentUserProfileState.Failure -> {
                    val error = (state as CurrentUserProfileState.Failure).error
                    Text("failed to login: $error")
                    Button(onClick = onFetchProfileFailed) { Text("Login again") }
                }
                is CurrentUserProfileState.Initial,
                is CurrentUserProfileState.Loading -> {
                    CircularProgressIndicator()
                }
                is CurrentUserProfileState.Success -> {
                    val username = state.username
                    Text("Hello $username")
                }
            }
        }
    }
}
