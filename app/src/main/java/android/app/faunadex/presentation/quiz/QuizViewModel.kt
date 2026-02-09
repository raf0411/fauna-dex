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
    }

    private fun loadUser() {
        android.util.Log.d("QuizViewModel", "loadUser() called")
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)
        android.util.Log.d("QuizViewModel", "User loaded: ${user?.uid}, type: ${user?.userType}")

        user?.let {
            if (it.userType == "Teacher") {
                android.util.Log.d("QuizViewModel", "Loading all quizzes for teacher")
                loadQuizzes(educationLevel = null)
            } else {
                android.util.Log.d("QuizViewModel", "Loading quizzes for education level: ${it.educationLevel}")
                loadQuizzesByEducationLevel(it.educationLevel)
            }
            android.util.Log.d("QuizViewModel", "Loading completed quizzes for user: ${it.uid}")
            loadCompletedQuizzes(it.uid)
        }
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

    private fun loadQuizzesByEducationLevel(educationLevel: String) {
        viewModelScope.launch {
            android.util.Log.d("QuizViewModel", "loadQuizzesByEducationLevel($educationLevel) called")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = getQuizzesUseCase(educationLevel)

            result.fold(
                onSuccess = { quizzes ->
                    android.util.Log.d("QuizViewModel", "Loaded ${quizzes.size} quizzes for education level: $educationLevel")
                    _uiState.value = _uiState.value.copy(
                        quizzes = quizzes,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizViewModel", "Error loading quizzes: ${exception.message}")
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
            android.util.Log.d("QuizViewModel", "loadCompletedQuizzes called for userId: $userId")
            val result = getUserCompletedQuizzesUseCase(userId)

            result.fold(
                onSuccess = { completedIds ->
                    android.util.Log.d("QuizViewModel", "Completed quizzes loaded: $completedIds")
                    _uiState.value = _uiState.value.copy(completedQuizIds = completedIds)
                    android.util.Log.d("QuizViewModel", "Current state completedQuizIds: ${_uiState.value.completedQuizIds}")
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizViewModel", "Failed to load completed quizzes: ${exception.message}")
                }
            )
        }
    }

    fun refreshCompletedQuizzes() {
        val userId = _uiState.value.user?.uid ?: return
        loadCompletedQuizzes(userId)
    }

    fun refresh() {
        android.util.Log.d("QuizViewModel", "refresh() called")
        loadUser()
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
