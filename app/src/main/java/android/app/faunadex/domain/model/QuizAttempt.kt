package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class QuizAttempt(
    val id: String = "",

    @PropertyName("user_id")
    val userId: String = "",

    @PropertyName("quiz_id")
    val quizId: String = "",

    val score: Int = 0,

    @PropertyName("total_questions")
    val totalQuestions: Int = 0,

    @PropertyName("correct_answers")
    val correctAnswers: Int = 0,

    @PropertyName("wrong_answers")
    val wrongAnswers: Int = 0,

    @PropertyName("completion_percentage")
    val completionPercentage: Int = 0,

    @PropertyName("xp_earned")
    val xpEarned: Int = 0,

    @PropertyName("time_taken_seconds")
    val timeTakenSeconds: Int = 0,

    @PropertyName("answers")
    val answers: Map<String, UserAnswer> = emptyMap(), // questionId -> UserAnswer

    @PropertyName("is_completed")
    val isCompleted: Boolean = false,

    @ServerTimestamp
    @PropertyName("started_at")
    val startedAt: Date? = null,

    @ServerTimestamp
    @PropertyName("completed_at")
    val completedAt: Date? = null
)

data class UserAnswer(
    @PropertyName("question_id")
    val questionId: String = "",

    @PropertyName("selected_answer_index")
    val selectedAnswerIndex: Int = -1,

    @PropertyName("is_correct")
    val isCorrect: Boolean = false,

    @PropertyName("time_taken_seconds")
    val timeTakenSeconds: Int = 0
)
