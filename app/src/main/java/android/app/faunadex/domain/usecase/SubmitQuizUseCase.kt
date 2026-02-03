package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.QuizAttempt
import android.app.faunadex.domain.model.UserAnswer
import android.app.faunadex.domain.repository.QuizRepository
import android.app.faunadex.domain.repository.UserRepository
import javax.inject.Inject

class SubmitQuizUseCase @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        attempt: QuizAttempt,
        xpEarned: Int
    ): Result<Unit> {
        return try {
            // Submit quiz attempt
            val submitResult = quizRepository.submitQuizAttempt(attempt)
            if (submitResult.isFailure) {
                return submitResult
            }

            // Update user XP
            val userProfile = userRepository.getUserProfile(attempt.userId).getOrNull()
            if (userProfile != null) {
                val updatedUser = userProfile.copy(
                    totalXp = userProfile.totalXp + xpEarned
                )
                userRepository.updateUserProfile(updatedUser)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calculate score based on correct answers and time taken
     * Score = (correct/total) * 100
     */
    fun calculateScore(correctAnswers: Int, totalQuestions: Int): Int {
        if (totalQuestions == 0) return 0
        return ((correctAnswers.toDouble() / totalQuestions) * 100).toInt()
    }

    /**
     * Calculate XP earned based on score and difficulty
     */
    fun calculateXpEarned(score: Int, baseXp: Int): Int {
        // XP = baseXp * (score / 100)
        // Minimum 20% of base XP even if score is low
        val percentage = score / 100.0
        val earnedXp = (baseXp * percentage).toInt()
        val minimumXp = (baseXp * 0.2).toInt()
        return earnedXp.coerceAtLeast(minimumXp)
    }
}
