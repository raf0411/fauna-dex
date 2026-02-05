package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.QuizAttempt
import android.app.faunadex.domain.model.UserAnswer
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.usecase.GetQuizQuestionsUseCase
import android.app.faunadex.domain.usecase.SubmitQuizUseCase
import android.app.faunadex.domain.repository.QuizRepository
import android.app.faunadex.utils.QuizMusicPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizGameplayUiState(
    val quiz: Quiz? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isRevealed: Boolean = false,
    val timeRemaining: Int = 30,
    val userAnswers: Map<String, UserAnswer> = emptyMap(),
    val attemptId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isQuizCompleted: Boolean = false,
    val isMuted: Boolean = false
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val correctAnswers: Int
        get() = userAnswers.values.count { it.isCorrect }

    val wrongAnswers: Int
        get() = userAnswers.values.count { !it.isCorrect }

    val progress: Float
        get() = if (questions.isNotEmpty()) {
            currentQuestionIndex.toFloat() / questions.size
        } else 0f
}

@HiltViewModel
class QuizGameplayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getQuizQuestionsUseCase: GetQuizQuestionsUseCase,
    private val quizRepository: QuizRepository,
    private val submitQuizUseCase: SubmitQuizUseCase,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: ""

    private val _uiState = MutableStateFlow(QuizGameplayUiState())
    val uiState: StateFlow<QuizGameplayUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var questionStartTime: Long = 0
    private var quizStartTime: Long = 0

    // Music player for background music
    private val musicPlayer: QuizMusicPlayer = QuizMusicPlayer(context, R.raw.quiz_background_music)

    init {
        loadQuizAndQuestions()
    }

    private fun loadQuizAndQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val quizResult = quizRepository.getQuizById(quizId)
            val quiz = quizResult.getOrNull()

            if (quiz == null) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.error_quiz_not_found),
                    isLoading = false
                )
                return@launch
            }

            val questionsResult = getQuizQuestionsUseCase(quizId)

            questionsResult.fold(
                onSuccess = { questions ->
                    android.util.Log.d("QuizGameplay", "Loaded ${questions.size} questions for quiz $quizId")
                    questions.forEach { q ->
                        android.util.Log.d("QuizGameplay", "  - Question: ${q.id} - ${q.questionTextEn.take(50)}")
                    }

                    val user = getCurrentUserUseCase()
                    if (user != null) {
                        val attemptResult = quizRepository.startQuizAttempt(user.uid, quizId)
                        val attempt = attemptResult.getOrNull()

                        _uiState.value = _uiState.value.copy(
                            quiz = quiz,
                            questions = questions,
                            attemptId = attempt?.id ?: "",
                            timeRemaining = quiz.timeLimitSeconds,
                            isLoading = false
                        )

                        quizStartTime = System.currentTimeMillis()
                        questionStartTime = System.currentTimeMillis()
                        startTimer()

                        // Start background music
                        musicPlayer.play()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = context.getString(R.string.error_user_not_logged_in),
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizGameplay", "Error loading questions: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_load_questions),
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && !_uiState.value.isRevealed) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timeRemaining = _uiState.value.timeRemaining - 1
                )
            }

            if (_uiState.value.timeRemaining <= 0 && !_uiState.value.isRevealed) {
                confirmAnswer()
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (!_uiState.value.isRevealed) {
            _uiState.value = _uiState.value.copy(selectedAnswerIndex = index)
        }
    }

    fun confirmAnswer() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        val selectedIndex = state.selectedAnswerIndex ?: -1

        val timeTaken = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()
        val isCorrect = selectedIndex == currentQuestion.correctAnswerIndex

        val userAnswer = UserAnswer(
            questionId = currentQuestion.id,
            selectedAnswerIndex = selectedIndex,
            isCorrect = isCorrect,
            timeTakenSeconds = timeTaken
        )

        val updatedAnswers = state.userAnswers + (currentQuestion.id to userAnswer)

        _uiState.value = state.copy(
            isRevealed = true,
            userAnswers = updatedAnswers
        )

        timerJob?.cancel()
    }

    fun nextQuestion() {
        val state = _uiState.value

        if (state.currentQuestionIndex < state.questions.size - 1) {
            _uiState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedAnswerIndex = null,
                isRevealed = false,
                timeRemaining = state.quiz?.timeLimitSeconds ?: 30
            )

            questionStartTime = System.currentTimeMillis()
            startTimer()
        } else {
            submitQuiz()
        }
    }

    fun pauseMusic() {
        musicPlayer.pause()
    }

    fun resumeMusic() {
        if (!_uiState.value.isQuizCompleted && !_uiState.value.isMuted) {
            musicPlayer.play()
        }
    }

    fun toggleMute() {
        val newMutedState = !_uiState.value.isMuted
        _uiState.value = _uiState.value.copy(isMuted = newMutedState)

        if (newMutedState) {
            musicPlayer.pause()
        } else {
            if (!_uiState.value.isQuizCompleted) {
                musicPlayer.play()
            }
        }
    }

    private fun submitQuiz() {
        viewModelScope.launch {
            android.util.Log.d("QuizGameplay", "submitQuiz() called")
            val state = _uiState.value
            val user = getCurrentUserUseCase()
            val quiz = state.quiz

            if (user == null || quiz == null) {
                android.util.Log.e("QuizGameplay", "submitQuiz failed - user or quiz is null")
                return@launch
            }

            val totalTimeTaken = ((System.currentTimeMillis() - quizStartTime) / 1000).toInt()
            val correctCount = state.correctAnswers
            val wrongCount = state.wrongAnswers
            val totalQuestions = state.questions.size

            android.util.Log.d("QuizGameplay", "Quiz submission data:")
            android.util.Log.d("QuizGameplay", "  - correctCount: $correctCount")
            android.util.Log.d("QuizGameplay", "  - wrongCount: $wrongCount")
            android.util.Log.d("QuizGameplay", "  - totalQuestions: $totalQuestions")

            val score = submitQuizUseCase.calculateScore(correctCount, totalQuestions)
            val xpEarned = submitQuizUseCase.calculateXpEarned(score, quiz.xpReward)
            val completionPercentage = ((correctCount.toDouble() / totalQuestions) * 100).toInt()

            val attempt = QuizAttempt(
                id = state.attemptId,
                userId = user.uid,
                quizId = quiz.id,
                score = score,
                totalQuestions = totalQuestions,
                correctAnswers = correctCount,
                wrongAnswers = wrongCount,
                completionPercentage = completionPercentage,
                xpEarned = xpEarned,
                timeTakenSeconds = totalTimeTaken,
                answers = state.userAnswers,
                isCompleted = true
            )

            android.util.Log.d("QuizGameplay", "Attempting to submit: ${attempt}")

            val result = submitQuizUseCase(attempt, xpEarned)

            result.fold(
                onSuccess = {
                    android.util.Log.d("QuizGameplay", "Quiz submitted successfully!")

                    // Stop music when quiz is completed
                    musicPlayer.stop()

                    _uiState.value = state.copy(isQuizCompleted = true)
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizGameplay", "Failed to submit quiz: ${exception.message}")
                    _uiState.value = state.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_submit_quiz)
                    )
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()

        // Release music player resources
        musicPlayer.release()
    }
}
