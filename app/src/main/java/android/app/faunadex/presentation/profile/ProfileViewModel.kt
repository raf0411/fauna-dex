package android.app.faunadex.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import android.app.faunadex.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val result = getUserProfileUseCase(currentUser.uid)

                result.onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                }.onFailure { exception ->
                    _uiState.value = ProfileUiState.Error(
                        exception.message ?: "Failed to load profile"
                    )
                }
            } else {
                _uiState.value = ProfileUiState.Error("User not logged in")
            }
        }
    }

    fun retry() {
        loadUserProfile()
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

