package android.app.faunadex.domain.repository

import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.QuizAttempt

interface QuizRepository {
    // Quiz operations
    suspend fun getQuizzes(educationLevel: String? = null): Result<List<Quiz>>
    suspend fun getQuizById(quizId: String): Result<Quiz>
    suspend fun getQuizzesByCategory(category: String): Result<List<Quiz>>

    // Question operations
    suspend fun getQuestionsByQuizId(quizId: String): Result<List<Question>>
    suspend fun getQuestionById(questionId: String): Result<Question>

    // Quiz attempt operations
    suspend fun startQuizAttempt(userId: String, quizId: String): Result<QuizAttempt>
    suspend fun submitQuizAttempt(attempt: QuizAttempt): Result<Unit>
    suspend fun getUserQuizAttempts(userId: String, quizId: String? = null): Result<List<QuizAttempt>>
    suspend fun getUserCompletedQuizIds(userId: String): Result<List<String>>

    // Statistics
    suspend fun getUserQuizStats(userId: String): Result<QuizStats>
}

data class QuizStats(
    val totalQuizzesTaken: Int = 0,
    val totalQuizzesCompleted: Int = 0,
    val totalXpEarned: Int = 0,
    val averageScore: Double = 0.0,
    val bestScore: Int = 0
)
