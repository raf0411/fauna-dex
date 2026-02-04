package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Quiz(
    val id: String = "",

    @PropertyName("title_en")
    val titleEn: String = "",

    @PropertyName("title_id")
    val titleId: String = "",

    @PropertyName("image_url")
    val imageUrl: String = "",

    @PropertyName("description_en")
    val descriptionEn: String = "",

    @PropertyName("description_id")
    val descriptionId: String = "",

    @PropertyName("total_questions")
    val totalQuestions: Int = 0,

    @PropertyName("education_level")
    val educationLevel: String = "",

    @PropertyName("category")
    val category: String = "",

    @PropertyName("difficulty")
    val difficulty: String = "medium", // easy, medium, hard

    @PropertyName("xp_reward")
    val xpReward: Int = 100,

    @PropertyName("time_limit_seconds")
    val timeLimitSeconds: Int = 30,

    @PropertyName("question_ids")
    val questionIds: List<String> = emptyList(),

    @PropertyName("is_active")
    val isActive: Boolean = true,

    @ServerTimestamp
    @PropertyName("created_at")
    val createdAt: Date? = null
) {
    // Get title based on language
    fun getTitle(language: String): String {
        return when (language) {
            "id" -> titleId
            else -> titleEn
        }
    }

    // Get description based on language
    fun getDescription(language: String): String {
        return when (language) {
            "id" -> descriptionId
            else -> descriptionEn
        }
    }
}
