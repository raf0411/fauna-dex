package android.app.faunadex.presentation.dashboard

import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.FaunaTopBar
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenShade
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            onNavigateToLogin()
        }
    }

    DashboardScreenContent(
        uiState = uiState,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToProfile: () -> Unit,
    currentRoute: String = "dashboard"
) {
    Scaffold(
        topBar = {
            FaunaTopBar(backgroundColor = PrimaryGreen)
        },
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "quiz" -> { /* TODO: Navigate to quiz */ }
                        "dashboard" -> { /* Already on dashboard */ }
                    }
                }
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    label = "Search your Fauna...",
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(start = 6.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DarkGreenShade
                        )
                    }
                )

                IconButton(
                    onClick = { /* TODO: Handle filter click */ }
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Dashboard",
                fontSize = 64.sp,
                fontFamily = JerseyFont,
                color = PastelYellow,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                user = User(
                    uid = "abc123xyz456",
                    email = "test@example.com",
                    username = "TestUser"
                ),
                isSignedOut = false
            ),
            onNavigateToProfile = {}
        )
    }
}

