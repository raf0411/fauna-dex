package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage

@Composable
fun QuizDetailScreen(
    onNavigateBack: () -> Unit = {},
    onStartQuiz: (String) -> Unit = {},
    viewModel: QuizDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = uiState.quiz?.title ?: stringResource(R.string.quiz_details),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: stringResource(R.string.error_unknown),
                        color = PastelYellow
                    )
                }
            }
            uiState.quiz != null -> {
                QuizDetailContent(
                    quiz = uiState.quiz!!,
                    onStartQuiz = { onStartQuiz(uiState.quiz!!.id) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}


@Composable
private fun QuizDetailContent(
    quiz: Quiz,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        SubcomposeAsyncImage(
            model = quiz.imageUrl,
            contentDescription = quiz.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MediumGreenSage.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.animal_dummy),
                        contentDescription = quiz.title,
                        tint = PastelYellow,
                        modifier = Modifier.size(80.dp)
                    )
                }
            },
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MediumGreenSage.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingSpinner(size = 48.dp, strokeWidth = 4.dp)
                }
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 240.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = DarkForest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp,
                        top = 24.dp
                    )
            ) {
                Text(
                    text = stringResource(R.string.quiz_title_animal).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MediumGreenSage
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = quiz.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelYellow
                )

                Spacer(modifier = Modifier.height(16.dp))

                QuizInfoCard(
                    totalQuestions = quiz.totalQuestions,
                    educationLevel = quiz.educationLevel
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.quiz_description_title).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MediumGreenSage
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = quiz.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = PastelYellow,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(50.dp))

                IconButton(
                    onClick = onStartQuiz,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(84.dp)
                        .background(
                            color = PrimaryGreen,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.start_quiz),
                        tint = PastelYellow,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun QuizInfoCard(
    totalQuestions: Int,
    educationLevel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuizInfoItem(
                icon = Icons.Default.QuestionMark,
                label = stringResource(R.string.questions),
                value = totalQuestions.toString(),
                backgroundColor = PrimaryGreenLight
            )

            Spacer(modifier = Modifier.size(32.dp))

            QuizInfoItem(
                icon = Icons.Default.School,
                label = "",
                value = educationLevel,
                backgroundColor = PrimaryGreenLight
            )
        }
    }
}

@Composable
private fun QuizInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = DarkForest,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = "$value $label",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFont,
            color = PrimaryGreenLight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizDetailScreenPreview() {
    FaunaDexTheme {
        val sampleQuiz = Quiz(
            id = "quiz_1",
            title = "Animal Habitats",
            imageUrl = "https://images.unsplash.com/photo-1446891574402-9b1e68b5e58e",
            description = "Test your knowledge about different animal habitats around the world.",
            totalQuestions = 10,
            educationLevel = "SMP",
            category = "Habitat",
            difficulty = "easy",
            xpReward = 100,
            timeLimitSeconds = 30
        )

        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = sampleQuiz.title,
                    onNavigateBack = {}
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            QuizDetailContent(
                quiz = sampleQuiz,
                onStartQuiz = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
