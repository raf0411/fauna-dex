package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.TopAppBar
import android.app.faunadex.presentation.components.TopAppBarUserData
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentRoute: String = "quiz"
) {
    val mockUserData = TopAppBarUserData(
        username = "User",
        profilePictureUrl = null,
        educationLevel = "SMA",
        currentLevel = 1,
        currentXp = 0,
        xpForNextLevel = 1000
    )

    QuizScreenContent(
        userData = mockUserData,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToProfile = onNavigateToProfile,
        currentRoute = currentRoute
    )
}

@Composable
fun QuizScreenContent(
    userData: TopAppBarUserData,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentRoute: String = "quiz"
) {
    Scaffold(
        topBar = {
            TopAppBar(userData = userData)
        },
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> onNavigateToDashboard()
                        "profile" -> onNavigateToProfile()
                        "quiz" -> { /* Already on quiz */ }
                    }
                }
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        QuizContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun QuizContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = null,
                tint = PastelYellow,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.quiz_feature),
                color = PastelYellow,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = JerseyFont
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.coming_soon),
                color = PrimaryGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = JerseyFont
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.quiz_description),
                color = PastelYellow.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    FaunaDexTheme {
        QuizScreenContent(
            userData = TopAppBarUserData(
                username = "raf_0411",
                profilePictureUrl = null,
                educationLevel = "SMA",
                currentLevel = 5,
                currentXp = 450,
                xpForNextLevel = 1000
            ),
            onNavigateToDashboard = {},
            onNavigateToProfile = {}
        )
    }
}
