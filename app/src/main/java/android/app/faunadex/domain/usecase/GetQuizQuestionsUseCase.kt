package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizQuestionsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(quizId: String): Result<List<Question>> {
        return quizRepository.getQuestionsByQuizId(quizId)
    }
}
