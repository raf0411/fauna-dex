package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName

data class Question(
    val id: String = "",

    @PropertyName("quiz_id")
    val quizId: String = "",

    @PropertyName("question_text_en")
    val questionTextEn: String = "",

    @PropertyName("question_text_id")
    val questionTextId: String = "",

    @PropertyName("question_type")
    val questionType: String = "multiple_choice",

    @PropertyName("options_en")
    val optionsEn: List<String> = emptyList(),

    @PropertyName("options_id")
    val optionsId: List<String> = emptyList(),

    @PropertyName("correct_answer_index")
    val correctAnswerIndex: Int = 0,

    @PropertyName("explanation_en")
    val explanationEn: String = "",

    @PropertyName("explanation_id")
    val explanationId: String = "",

    @PropertyName("difficulty")
    val difficulty: String = "medium",

    @PropertyName("order_index")
    val orderIndex: Int = 0
) {
    fun getQuestionText(language: String): String {
        return when (language) {
            "id" -> questionTextId
            else -> questionTextEn
        }
    }

    fun getOptions(language: String): List<String> {
        return when (language) {
            "id" -> optionsId
            else -> optionsEn
        }
    }

    fun getExplanation(language: String): String {
        return when (language) {
            "id" -> explanationId
            else -> explanationEn
        }
    }
}
