package android.app.faunadex.presentation.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import android.app.faunadex.domain.repository.UserRepository
import android.app.faunadex.domain.usecase.GetUserProfileUseCase
import android.app.faunadex.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val signOutUseCase: SignOutUseCase,
    @ApplicationContext private val context: Context
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
            Log.d("ProfileViewModel", "Current user from Auth: uid=${currentUser?.uid}, email=${currentUser?.email}")

            if (currentUser != null) {
                val result = getUserProfileUseCase(currentUser.uid)

                result.onSuccess { user ->
                    Log.d("ProfileViewModel", "User profile fetched: username=${user.username}, educationLevel=${user.educationLevel}, xp=${user.totalXp}")
                    _uiState.value = ProfileUiState.Success(user)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to fetch profile", exception)
                    _uiState.value = ProfileUiState.Error(
                        exception.message ?: "Failed to load profile"
                    )
                }
            } else {
                Log.e("ProfileViewModel", "No current user found")
                _uiState.value = ProfileUiState.Error("User not logged in")
            }
        }
    }

    fun updateEducationLevel(educationLevel: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                _uiState.value = ProfileUiState.Loading

                val updatedUser = currentState.user.copy(educationLevel = educationLevel)
                val result = userRepository.updateUserProfile(updatedUser)

                result.onSuccess {
                    Log.d("ProfileViewModel", "Education level updated to: $educationLevel")
                    _uiState.value = ProfileUiState.Success(updatedUser)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to update education level", exception)
                    _uiState.value = ProfileUiState.Error(
                        "Failed to update: ${exception.message}"
                    )
                }
            }
        }
    }

    fun retry() {
        loadUserProfile()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                Log.d("ProfileViewModel", "User logged out successfully")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to logout", e)
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    _uiState.value = ProfileUiState.Loading

                    val result = userRepository.uploadProfilePicture(currentUser.uid, imageUri, context)

                    result.onSuccess { base64Image ->
                        Log.d("ProfileViewModel", "Profile picture uploaded as Base64")
                        val updatedUser = currentState.user.copy(profilePictureUrl = base64Image)
                        _uiState.value = ProfileUiState.Success(updatedUser)
                    }.onFailure { exception ->
                        Log.e("ProfileViewModel", "Failed to upload profile picture", exception)
                        _uiState.value = ProfileUiState.Error(
                            "Failed to upload profile picture: ${exception.message}"
                        )
                        // Restore previous state after a short delay
                        kotlinx.coroutines.delay(2000)
                        _uiState.value = currentState
                    }
                }
            }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
