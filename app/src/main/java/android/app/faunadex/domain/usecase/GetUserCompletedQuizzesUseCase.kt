package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.repository.QuizRepository
import javax.inject.Inject

class GetUserCompletedQuizzesUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(userId: String): Result<List<String>> {
        return quizRepository.getUserCompletedQuizIds(userId)
    }
}
