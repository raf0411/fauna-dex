package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.ui.theme.DarkForest
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentRoute: String = "quiz"
) {
    Scaffold(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
