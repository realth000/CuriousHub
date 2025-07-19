package kzs.th000.curioushub.features.profile.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kzs.th000.curioushub.data.database.dao.UserDao
import kzs.th000.curioushub.features.profile.state.CurrentUserProfileState

class CurrentUserProfileViewModel(
    private val userDao: UserDao,
    private val initialState: CurrentUserProfileState,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()
}
