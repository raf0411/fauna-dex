package android.app.faunadex.presentation.profile

import android.app.faunadex.R
import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.ConfirmationDialog
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.EducationLevelSelector
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val profileUpdatedMessage = stringResource(R.string.profile_updated_successfully)

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = stringResource(R.string.edit_profile),
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
                    LoadingSpinner()
                }
            }
            is ProfileUiState.Success -> {
                EditProfileContent(
                    user = (uiState as ProfileUiState.Success).user,
                    onSave = { username, educationLevel ->
                        viewModel.updateProfile(username, educationLevel)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = profileUpdatedMessage,
                                duration = SnackbarDuration.Short
                            )
                            kotlinx.coroutines.delay(1000)
                        }
                    },
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
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf(user.username) }
    var educationLevel by remember { mutableStateOf(user.educationLevel) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEducationLevelDialog by remember { mutableStateOf(false) }
    var pendingUsername by remember { mutableStateOf("") }
    var pendingEducationLevel by remember { mutableStateOf("") }

    val usernameLabel = stringResource(R.string.username)
    val educationLevelLabel = stringResource(R.string.your_education_level)
    val saveLabel = stringResource(R.string.save)
    val editProfileTitle = stringResource(R.string.edit_profile)
    val editProfileMessage = stringResource(R.string.edit_profile_confirmation)
    val confirmLabel = stringResource(R.string.confirm)
    val cancelLabel = stringResource(R.string.cancel)
    val changingEducationTitle = stringResource(R.string.changing_education_level)
    val educationChangeWarning = stringResource(R.string.education_level_change_warning)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = usernameLabel,
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
            label = usernameLabel
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = educationLevelLabel,
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

        Button(
            onClick = {
                pendingUsername = username
                pendingEducationLevel = educationLevel
                showEditProfileDialog = true
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
                text = saveLabel,
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = PastelYellow
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    ConfirmationDialog(
        title = editProfileTitle,
        message = editProfileMessage,
        confirmText = confirmLabel,
        cancelText = cancelLabel,
        onConfirm = {
            if (pendingEducationLevel != user.educationLevel) {
                showEducationLevelDialog = true
            } else {
                onSave(pendingUsername, pendingEducationLevel)
            }
        },
        onDismiss = { showEditProfileDialog = false },
        showDialog = showEditProfileDialog
    )

    ConfirmationDialog(
        title = changingEducationTitle,
        message = educationChangeWarning,
        confirmText = confirmLabel,
        cancelText = cancelLabel,
        onConfirm = {
            onSave(pendingUsername, pendingEducationLevel)
            showEditProfileDialog = false
        },
        onDismiss = {
            showEducationLevelDialog = false
            showEditProfileDialog = false
        },
        showDialog = showEducationLevelDialog
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
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
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

