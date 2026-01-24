package android.app.faunadex

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.app.faunadex.presentation.navigation.NavGraph
import android.app.faunadex.presentation.navigation.Screen
import android.app.faunadex.ui.theme.FaunaDexTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaunaDexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FaunaDexApp(
                        modifier = Modifier.padding(innerPadding),
                        isUserLoggedIn = firebaseAuth.currentUser != null
                    )
                }
            }
        }
    }
}

private val mainScreenRoutes = listOf(
    Screen.Dashboard.route,
    Screen.Profile.route,
    Screen.Quiz.route
)

@Composable
fun FaunaDexApp(
    modifier: Modifier = Modifier,
    isUserLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val startDestination = if (isUserLoggedIn) {
        Screen.Dashboard.route
    } else {
        Screen.Onboarding.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    val backPressThreshold = 2000L // 2 seconds

    val isOnMainScreen = currentRoute in mainScreenRoutes

    BackHandler(enabled = isOnMainScreen) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < backPressThreshold) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(
                context,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
