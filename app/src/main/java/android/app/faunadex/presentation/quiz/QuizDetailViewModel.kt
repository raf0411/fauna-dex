package android.app.faunadex.presentation.quiz

import android.annotation.SuppressLint
import android.app.faunadex.R
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.repository.QuizRepository
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizDetailUiState(
    val quiz: Quiz? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QuizDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: ""

    private val _uiState = MutableStateFlow(QuizDetailUiState())
    val uiState: StateFlow<QuizDetailUiState> = _uiState.asStateFlow()

    init {
        loadQuizDetails()
    }

    private fun loadQuizDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = quizRepository.getQuizById(quizId)

            result.fold(
                onSuccess = { quiz ->
                    _uiState.value = _uiState.value.copy(
                        quiz = quiz,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_load_quiz_details),
                        isLoading = false
                    )
                }
            )
        }
    }

    fun retry() {
        loadQuizDetails()
    }
}
