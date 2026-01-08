package android.app.faunadex.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val educationLevel: String = "",
    val currentTitle: String = "Petualang Pemula",
    val totalXp: Int = 0,

    @ServerTimestamp
    val joinedAt: Date? = null
)

