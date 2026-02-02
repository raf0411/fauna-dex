package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.TopAppBar
import android.app.faunadex.presentation.components.TopAppBarUserData
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        SectionTitle(title = stringResource(R.string.quiz_available))

        Spacer(modifier = Modifier.height(12.dp))

        QuizCardItem(
            title = "Animal Habitats",
            subtitle = "Learn about different habitats",
            totalQuestions = 10,
            imageUrl = "https://images.unsplash.com/photo-1446891574402-9b1e68b5e58e?w=400&h=250&fit=crop",
            isCompleted = false,
            onCardClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuizCardItem(
            title = "Species Classification",
            subtitle = "Test your knowledge on species types",
            totalQuestions = 15,
            imageUrl = "https://images.unsplash.com/photo-1444080748397-f442aa95c3e5?w=400&h=250&fit=crop",
            isCompleted = false,
            onCardClick = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(title = stringResource(R.string.quiz_completed))

        Spacer(modifier = Modifier.height(12.dp))

        QuizCardItem(
            title = "Endangered Animals",
            subtitle = "Learn about endangered species",
            totalQuestions = 8,
            imageUrl = "https://images.unsplash.com/photo-1500463959177-e0869687df26?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            isCompleted = true,
            onCardClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuizCardItem(
            title = "Bird Species",
            subtitle = "Identify different bird species",
            totalQuestions = 12,
            imageUrl = "https://images.unsplash.com/photo-1500463959177-e0869687df26?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            isCompleted = true,
            onCardClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        color = PastelYellow,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun QuizCardItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    totalQuestions: Int,
    imageUrl: String,
    isCompleted: Boolean = false,
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MediumGreenSage.copy(alpha = 0.2f)
            else
                MediumGreenSage.copy(alpha = 0.1f)
        ),
        onClick = if (!isCompleted) onCardClick else { {} }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MediumGreenSage.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Construction,
                                contentDescription = null,
                                tint = PastelYellow,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryGreen.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = PastelYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = if (isCompleted)
                        PastelYellow.copy(alpha = 0.6f)
                    else
                        PastelYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(0.5.dp))

                Text(
                    text = subtitle,
                    color = if (isCompleted)
                        PrimaryGreen.copy(alpha = 0.5f)
                    else
                        PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    fontFamily = JerseyFont
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$totalQuestions questions",
                    color = if (isCompleted)
                        MediumGreenSage.copy(alpha = 0.4f)
                    else
                        MediumGreenSage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = JerseyFont
                )
            }

            if (!isCompleted) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open quiz",
                    tint = PastelYellow,
                    modifier = Modifier.size(32.dp)
                )
            }
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
