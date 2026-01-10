package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateUserProfile(user)
    }
}

