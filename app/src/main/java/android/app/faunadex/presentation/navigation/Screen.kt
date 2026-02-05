package android.app.faunadex.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AnimalDetail : Screen("animal_detail/{animalId}") {
        fun createRoute(animalId: String) = "animal_detail/$animalId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    object Quiz : Screen("quiz")
    object QuizDetail : Screen("quiz_detail/{quizId}") {
        fun createRoute(quizId: String) = "quiz_detail/$quizId"
    }
    object QuizGameplay : Screen("quiz_gameplay/{quizId}") {
        fun createRoute(quizId: String) = "quiz_gameplay/$quizId"
    }
    object QuizResult : Screen("quiz_result/{quizId}/{score}/{correctAnswers}/{wrongAnswers}/{totalQuestions}/{xpEarned}") {
        fun createRoute(
            quizId: String,
            score: Int,
            correctAnswers: Int,
            wrongAnswers: Int,
            totalQuestions: Int,
            xpEarned: Int
        ) = "quiz_result/$quizId/$score/$correctAnswers/$wrongAnswers/$totalQuestions/$xpEarned"
    }
    object AR : Screen("ar/{animalId}") {
        fun createRoute(animalId: String) = "ar/$animalId"
    }
}

