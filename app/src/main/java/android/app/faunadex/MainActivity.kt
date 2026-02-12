package android.app.faunadex

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.app.faunadex.presentation.navigation.NavGraph
import android.app.faunadex.presentation.navigation.Screen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.utils.LanguageManager
import android.app.faunadex.utils.PermissionsManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var showLanguageSnackbar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        applySavedLocale()

        showLanguageSnackbar = LanguageManager.shouldShowLanguageChangedSnackbar(applicationContext)
        if (showLanguageSnackbar) {
            LanguageManager.clearLanguageChangedSnackbarFlag(applicationContext)
        }

        enableEdgeToEdge()

        setContent {
            FaunaDexTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val languageChangedMessage = stringResource(R.string.language_changed_successfully)

                LaunchedEffect(Unit) {
                    if (showLanguageSnackbar) {
                        snackbarHostState.showSnackbar(
                            message = languageChangedMessage,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = PrimaryGreen,
                                contentColor = PastelYellow
                            )
                        }
                    }
                ) { innerPadding ->
                    FaunaDexApp(
                        modifier = Modifier.padding(innerPadding),
                        isUserLoggedIn = firebaseAuth.currentUser != null
                    )
                }
            }
        }
    }

    private fun applySavedLocale() {
        val savedLanguage = LanguageManager.getLanguage(applicationContext)
        Log.d("MainActivity", "applySavedLocale: Saved language = $savedLanguage")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = getSystemService(LocaleManager::class.java)
            val currentLocales = localeManager.applicationLocales
            Log.d("MainActivity", "applySavedLocale: Current app locales = $currentLocales")

            if (currentLocales.isEmpty) {
                Log.d("MainActivity", "applySavedLocale: Setting locale to $savedLanguage")
                localeManager.applicationLocales = LocaleList.forLanguageTags(savedLanguage)
            }
        }
    }
}

private val mainScreenRoutes = listOf(
    Screen.Dashboard.route,
    Screen.Profile.route,
    Screen.Quiz.route,
    Screen.Credits.route
)

@Composable
fun FaunaDexApp(
    modifier: Modifier = Modifier,
    isUserLoggedIn: Boolean
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val startDestination = if (isUserLoggedIn) {
        // If user is logged in, check if they've been through permissions flow
        if (PermissionsManager.hasRequestedPermissions(context)) {
            Screen.Dashboard.route
        } else {
            Screen.Permissions.route
        }
    } else {
        Screen.Onboarding.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    val backPressThreshold = 2000L
    val pressBackToExitMessage = stringResource(R.string.press_back_to_exit)

    val isOnMainScreen = currentRoute in mainScreenRoutes

    BackHandler(enabled = isOnMainScreen) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < backPressThreshold) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(
                context,
                pressBackToExitMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
