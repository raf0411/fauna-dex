package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import androidx.lifecycle.ViewModel
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.usecase.GetQuizzesUseCase
import android.app.faunadex.domain.usecase.GetUserCompletedQuizzesUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getQuizzesUseCase: GetQuizzesUseCase,
    private val getUserCompletedQuizzesUseCase: GetUserCompletedQuizzesUseCase,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadQuizzes()
    }

    private fun loadUser() {
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)

        user?.let { loadCompletedQuizzes(it.uid) }
    }

    fun loadQuizzes(educationLevel: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = getQuizzesUseCase(educationLevel)

            result.fold(
                onSuccess = { quizzes ->
                    _uiState.value = _uiState.value.copy(
                        quizzes = quizzes,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_load_quizzes),
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun loadCompletedQuizzes(userId: String) {
        viewModelScope.launch {
            val result = getUserCompletedQuizzesUseCase(userId)

            result.fold(
                onSuccess = { completedIds ->
                    _uiState.value = _uiState.value.copy(completedQuizIds = completedIds)
                },
                onFailure = { exception ->
                    println("Failed to load completed quizzes: ${exception.message}")
                }
            )
        }
    }

    fun refresh() {
        loadUser()
        loadQuizzes()
    }
}

data class QuizUiState(
    val user: User? = null,
    val quizzes: List<Quiz> = emptyList(),
    val completedQuizIds: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val availableQuizzes: List<Quiz>
        get() = quizzes.filter { it.id !in completedQuizIds }

    val completedQuizzes: List<Quiz>
        get() = quizzes.filter { it.id in completedQuizIds }
}
