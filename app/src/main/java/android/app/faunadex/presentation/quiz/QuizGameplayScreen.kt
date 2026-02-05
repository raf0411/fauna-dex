package android.app.faunadex.presentation.quiz

import android.annotation.SuppressLint
import android.app.faunadex.R
import android.app.faunadex.presentation.components.ConfirmationDialog
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.utils.QuizLanguageHelper
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.DarkGreenMoss
import android.app.faunadex.ui.theme.ErrorRedDark
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenAlpha60
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.PrimaryGreenNeon
import android.app.faunadex.ui.theme.QuizGreenGradient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun QuizGameplayScreen(
    onNavigateBack: () -> Unit = {},
    onQuizCompleted: (score: Int, correctAnswers: Int, wrongAnswers: Int, totalQuestions: Int) -> Unit = { _, _, _, _ -> },
    viewModel: QuizGameplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showQuitDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pauseMusic()
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.resumeMusic()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = stringResource(R.string.quiz_gameplay),
                onNavigateBack = { showQuitDialog = true },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleMute() },
                        modifier = Modifier.padding(end = 16.dp),
                        icon = if (uiState.isMuted) {
                            Icons.AutoMirrored.Filled.VolumeOff
                        } else {
                            Icons.AutoMirrored.Filled.VolumeUp
                        },
                        iconTint = PrimaryGreenLight,
                        backgroundColor = PrimaryGreenAlpha60,
                        borderColor = PrimaryGreenAlpha60,
                        borderWidth = 0.dp,
                        size = 48.dp,
                        iconSize = 24.dp,
                        contentDescription = if (uiState.isMuted) "Unmute" else "Mute"
                    )
                }
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = PrimaryGreen,
                            strokeWidth = 4.dp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Loading questions...",
                            color = PastelYellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error ?: "Unknown error", color = ErrorRedDark)
                }
            }
            else -> {
                QuizGameplayContent(
                    uiState = uiState,
                    onSelectAnswer = { index -> viewModel.selectAnswer(index) },
                    onConfirmAnswer = { viewModel.confirmAnswer() },
                    onNextQuestion = { viewModel.nextQuestion() },
                    onQuizCompleted = onQuizCompleted,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    ConfirmationDialog(
        title = stringResource(R.string.quit_quiz_title),
        message = stringResource(R.string.quit_quiz_message),
        confirmText = stringResource(R.string.quit),
        cancelText = stringResource(R.string.stay),
        onConfirm = {
            onNavigateBack()
        },
        onDismiss = { showQuitDialog = false },
        showDialog = showQuitDialog
    )
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun QuizGameplayContent(
    uiState: QuizGameplayUiState,
    onSelectAnswer: (Int) -> Unit,
    onConfirmAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onQuizCompleted: (score: Int, correctAnswers: Int, wrongAnswers: Int, totalQuestions: Int) -> Unit = { _, _, _, _ -> },
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.currentQuestion
    val isShowingConfetti = uiState.isRevealed && uiState.selectedAnswerIndex == currentQuestion?.correctAnswerIndex
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.currentQuestionIndex) {
        scrollState.animateScrollTo(0)
    }

    LaunchedEffect(uiState.isQuizCompleted) {
        if (uiState.isQuizCompleted) {
            onQuizCompleted(
                ((uiState.correctAnswers.toDouble() / uiState.questions.size) * 100).toInt(),
                uiState.correctAnswers,
                uiState.wrongAnswers,
                uiState.questions.size
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .background(
                        brush = QuizGreenGradient,
                        shape = RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f)
                    .background(DarkForest)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            if (currentQuestion != null) {
                QuestionBox(
                    timeRemaining = uiState.timeRemaining,
                    currentQuestion = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.questions.size,
                    questionText = QuizLanguageHelper.getQuestionText(currentQuestion, context)
                )

                Spacer(Modifier.height(64.dp))

                AnswerOptionsList(
                    answers = QuizLanguageHelper.getQuestionOptions(currentQuestion, context),
                    selectedAnswer = uiState.selectedAnswerIndex,
                    onAnswerSelected = { index -> onSelectAnswer(index) },
                    isRevealed = uiState.isRevealed,
                    correctAnswerIndex = currentQuestion.correctAnswerIndex
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!uiState.isRevealed) {
                        onConfirmAnswer()
                    } else {
                        onNextQuestion()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = if (uiState.isRevealed) {
                    uiState.canProceedToNext
                } else {
                    uiState.selectedAnswerIndex != null
                }
            ) {
                Text(
                    text = if (uiState.isRevealed) {
                        if (uiState.currentQuestionIndex >= uiState.questions.size - 1) {
                            stringResource(R.string.finish)
                        } else {
                            stringResource(R.string.next)
                        }
                    } else {
                        stringResource(R.string.confirm)
                    },
                    fontFamily = JerseyFont,
                    fontSize = 24.sp,
                    color = PastelYellow
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        if (isShowingConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(
                            0xFFBEDC7F,
                            0xFF89A257,
                            0xFFDBFB98,
                            0xFFEEFFCC,
                            0xFF71A8C6,
                            0xFFFB3434,
                            0xFFA5B08D,
                            0xFF00A63D
                        ).map { it.toInt() },
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.5, 0.3)
                    )
                )
            )
        }
    }
}

@Composable
fun QuestionBox(
    modifier: Modifier = Modifier,
    currentQuestion: Int = 8,
    totalQuestions: Int = 12,
    questionText: String = "What is the scientific name of the Komodo Dragon?",
    timeRemaining: Int = 30
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 40.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGreen
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.question))
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(currentQuestion.toString())
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 14.sp
                            )
                        ) {
                            append(" / $totalQuestions")
                        }
                    },
                    fontFamily = PoppinsFont,
                    fontWeight = FontWeight.SemiBold,
                    color = MediumGreenSage
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PastelYellow,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(4.dp))
            }
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = QuizGreenGradient,
                    shape = CircleShape
                )
                .border(
                    width = 4.dp,
                    color = DarkGreen,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            Text(
                text = "$minutes:${seconds.toString().padStart(2, '0')}",
                fontFamily = PoppinsFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MediumGreenSage
            )
        }
    }
}

@Composable
fun AnswerOptionsList(
    answers: List<String>,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    isRevealed: Boolean,
    correctAnswerIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        answers.forEachIndexed { index, answer ->
            AnswerOption(
                answer = answer,
                isSelected = selectedAnswer == index,
                onClick = { onAnswerSelected(index) },
                isRevealed = isRevealed,
                isCorrect = index == correctAnswerIndex,
                isWrong = isRevealed && selectedAnswer == index && index != correctAnswerIndex
            )
        }
    }
}

@Composable
fun AnswerOption(
    answer: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isRevealed: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isRevealed && isCorrect -> PrimaryGreenNeon
        isRevealed && isWrong -> ErrorRedDark
        isSelected -> DarkGreenMoss
        else -> PrimaryGreenLime
    }

    val indicatorBgColor = when {
        isRevealed && isWrong -> ErrorRedDark
        isRevealed && isCorrect -> DarkGreenMoss
        isSelected -> DarkGreenMoss
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    val indicatorBorderColor = when {
        isRevealed && isWrong -> androidx.compose.ui.graphics.Color.Transparent
        isSelected || (isRevealed && isCorrect) -> androidx.compose.ui.graphics.Color.Transparent
        else -> DarkForest
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isRevealed, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = DarkGreen,
        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = answer,
                fontFamily = JerseyFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = PastelYellow,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = indicatorBgColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = indicatorBorderColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isRevealed && isWrong -> {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Wrong",
                            tint = PrimaryGreenLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    isRevealed && isCorrect -> {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Correct",
                            tint = PrimaryGreenLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    isSelected -> {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = PrimaryGreenLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = PrimaryGreenLight,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizGameplayScreenPreview() {
    FaunaDexTheme {
        val mockQuestion = android.app.faunadex.domain.model.Question(
            id = "q1",
            quizId = "quiz_1",
            questionTextEn = "What is the scientific name of the Komodo Dragon?",
            questionTextId = "Apa nama ilmiah Komodo?",
            questionType = "multiple_choice",
            optionsEn = listOf(
                "Varanus komodoensis",
                "Varanus salvator",
                "Varanus gouldi",
                "Varanus acanthurus"
            ),
            optionsId = listOf(
                "Varanus komodoensis",
                "Varanus salvator",
                "Varanus gouldi",
                "Varanus acanthurus"
            ),
            correctAnswerIndex = 0,
            explanationEn = "Komodo is scientifically known as Varanus komodoensis.",
            explanationId = "Komodo secara ilmiah dikenal sebagai Varanus komodoensis.",
            difficulty = "medium",
            orderIndex = 0
        )

        val mockUiState = QuizGameplayUiState(
            quiz = null,
            questions = listOf(mockQuestion),
            currentQuestionIndex = 0,
            selectedAnswerIndex = null,
            isRevealed = false,
            timeRemaining = 120,
            userAnswers = emptyMap(),
            attemptId = "",
            isLoading = false,
            error = null,
            isQuizCompleted = false
        )

        QuizGameplayContent(
            uiState = mockUiState,
            onSelectAnswer = {},
            onConfirmAnswer = {},
            onNextQuestion = {},
            onQuizCompleted = { _, _, _, _ -> }
        )
    }
}