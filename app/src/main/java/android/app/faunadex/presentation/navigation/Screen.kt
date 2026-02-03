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
    object AR : Screen("ar/{animalId}") {
        fun createRoute(animalId: String) = "ar/$animalId"
    }
}

