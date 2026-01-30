package android.app.faunadex.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.app.faunadex.presentation.animalDetail.AnimalDetailScreen
import android.app.faunadex.presentation.ar.ArScreen
import android.app.faunadex.presentation.auth.login.LoginScreen
import android.app.faunadex.presentation.auth.onboarding.OnboardingScreen
import android.app.faunadex.presentation.auth.register.RegisterScreen
import android.app.faunadex.presentation.dashboard.DashboardScreen
import android.app.faunadex.presentation.profile.ChangePasswordScreen
import android.app.faunadex.presentation.profile.EditProfileScreen
import android.app.faunadex.presentation.profile.ProfileScreen
import android.app.faunadex.presentation.quiz.QuizScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToAnimalDetail = { animalId ->
                    navController.navigate(Screen.AnimalDetail.createRoute(animalId))
                }
            )
        }

        composable(
            route = Screen.AnimalDetail.route,
            arguments = listOf(
                navArgument("animalId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
            AnimalDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAr = { id ->
                    android.util.Log.d("NavGraph", "=== NAVIGATING TO AR ===")
                    android.util.Log.d("NavGraph", "Received animal ID from detail screen: '$id'")
                    android.util.Log.d("NavGraph", "Creating route: ${Screen.AR.createRoute(id)}")
                    navController.navigate(Screen.AR.createRoute(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Placeholder Quiz Screen - TODO: Implement actual Quiz feature
        composable(Screen.Quiz.route) {
            QuizScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.AR.route,
            arguments = listOf(
                navArgument("animalId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId")
            android.util.Log.d("NavGraph", "=== AR SCREEN COMPOSABLE ===")
            android.util.Log.d("NavGraph", "AnimalId from backStackEntry: '$animalId'")
            android.util.Log.d("NavGraph", "Is animalId null?: ${animalId == null}")
            android.util.Log.d("NavGraph", "Passing to ArScreen...")
            ArScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                animalId = animalId
            )
        }
    }
}

