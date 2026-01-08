package android.app.faunadex.presentation.profile

import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.EducationLevelSelector
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Date

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = "Edit Profile",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = PrimaryGreen,
                    contentColor = PastelYellow
                )
            }
        },
        containerColor = DarkForest
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = PastelYellow,
                        strokeWidth = 6.dp
                    )
                }
            }
            is ProfileUiState.Success -> {
                EditProfileContent(
                    user = (uiState as ProfileUiState.Success).user,
                    onSave = { username, educationLevel ->
                        viewModel.updateProfile(username, educationLevel)
                    },
                    onNavigateBack = onNavigateBack,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as ProfileUiState.Error).message,
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }
        }
    }
}


@Composable
private fun EditProfileContent(
    user: User,
    onSave: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf(user.username) }
    var educationLevel by remember { mutableStateOf(user.educationLevel) }
    var shouldNavigateBack by remember { mutableStateOf(false) }

    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) {
            snackbarHostState.showSnackbar(
                message = "Profile updated successfully!",
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(1000)
            onNavigateBack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Username",
            fontFamily = JerseyFont,
            fontSize = 24.sp,
            color = PastelYellow,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Education Level",
            fontFamily = JerseyFont,
            fontSize = 24.sp,
            color = PastelYellow,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        EducationLevelSelector(
            selectedLevel = educationLevel,
            onLevelSelected = { educationLevel = it },
            enabled = true
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Save Button
        Button(
            onClick = {
                onSave(username, educationLevel)
                shouldNavigateBack = true
            },
            modifier = Modifier
                .width(120.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = username.isNotBlank()
        ) {
            Text(
                text = "Save",
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = PastelYellow
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Edit Profile",
                    onNavigateBack = {}
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            EditProfileContent(
                user = User(
                    uid = "preview123",
                    email = "rafi@test.com",
                    username = "raf_0411",
                    educationLevel = "SMA",
                    currentTitle = "Petualang",
                    totalXp = 450,
                    joinedAt = Date()
                ),
                onSave = { _, _ -> },
                onNavigateBack = {},
                snackbarHostState = snackbarHostState,
                scope = scope,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

