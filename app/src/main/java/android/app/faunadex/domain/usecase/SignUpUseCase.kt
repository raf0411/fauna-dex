package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        educationLevel: String
    ): AuthResult<User> {
        // Validation logic
        if (email.isBlank() || password.isBlank() || username.isBlank() || educationLevel.isBlank()) {
            return AuthResult.Error("All fields are required")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Invalid email format")
        }
        if (username.length < 3) {
            return AuthResult.Error("Username must be at least 3 characters")
        }

        return authRepository.signUp(email, password, username, educationLevel)
    }
}

