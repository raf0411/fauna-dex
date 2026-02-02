package android.app.faunadex.presentation.quiz

import androidx.lifecycle.ViewModel
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)
    }
}

data class QuizUiState(
    val user: User? = null,
    val error: String? = null
)
