package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String): Result<User> {
        if (uid.isBlank()) {
            return Result.failure(Exception("User ID cannot be empty"))
        }
        return userRepository.getUserProfile(uid)
    }
}

