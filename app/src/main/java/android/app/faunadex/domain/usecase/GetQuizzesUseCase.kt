package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizzesUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(educationLevel: String? = null): Result<List<Quiz>> {
        return quizRepository.getQuizzes(educationLevel)
    }
}
