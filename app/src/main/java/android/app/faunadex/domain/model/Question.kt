package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName

data class Question(
    val id: String = "",

    @PropertyName("quiz_id")
    val quizId: String = "",

    @PropertyName("question_text")
    val questionText: String = "",

    @PropertyName("question_type")
    val questionType: String = "multiple_choice", // multiple_choice, true_false

    val options: List<String> = emptyList(),

    @PropertyName("correct_answer_index")
    val correctAnswerIndex: Int = 0,

    val explanation: String = "",

    @PropertyName("difficulty")
    val difficulty: String = "medium",

    @PropertyName("order_index")
    val orderIndex: Int = 0
)
