package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.ErrorRedDark
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenMint
import android.app.faunadex.ui.theme.MediumGreenPale
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLightAlpha10
import android.app.faunadex.ui.theme.PrimaryGreenLightAlpha30
import android.app.faunadex.ui.theme.PrimaryGreenNeon
import android.app.faunadex.ui.theme.QuizGreenGradient
import android.app.faunadex.ui.theme.White
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun QuizResultScreen(
    score: Int = 100,
    totalQuestions: Int = 10,
    correctAnswers: Int = 10,
    wrongAnswers: Int = 0,
    completionPercentage: Int = 100,
    onNavigateBack: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    onNavigateHome: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = stringResource(R.string.result),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        QuizResultContent(
            score = score,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            wrongAnswers = wrongAnswers,
            completionPercentage = completionPercentage,
            onPlayAgain = onPlayAgain,
            onNavigateHome = onNavigateHome,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun QuizResultContent(
    modifier: Modifier = Modifier,
    score: Int = 100,
    totalQuestions: Int = 10,
    correctAnswers: Int = 10,
    wrongAnswers: Int = 0,
    completionPercentage: Int = 100,
    onPlayAgain: () -> Unit = {},
    onNavigateHome: () -> Unit = {}
) {
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showConfetti = true
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
                    .weight(0.6f)
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
                    .weight(0.4f)
                    .background(DarkForest)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(30.dp))

            GlowingScoreCircle(score = score)

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.out_of_100),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PastelYellow
            )

            Spacer(Modifier.height(32.dp))

            QuizInfoBox(
                completionPercentage = completionPercentage,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                wrongAnswers = wrongAnswers
            )

            Spacer(Modifier.height(64.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onPlayAgain,
                        icon = Icons.Default.Replay,
                        iconTint = DarkForest
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.quiz_replay),
                        fontSize = 20.sp,
                        color = PrimaryGreen,
                        fontFamily = JerseyFont,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    IconButton(
                        onClick = onNavigateHome,
                        icon = Icons.Default.Home,
                        iconTint = DarkForest
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.nav_home),
                        fontSize = 20.sp,
                        color = PrimaryGreen,
                        fontFamily = JerseyFont,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 30f,
                        maxSpeed = 50f,
                        damping = 0.9f,
                        angle = 270,
                        spread = 90,
                        colors = listOf(
                            0xFFBEDC7F.toInt(),
                            0xFF89A257.toInt(),
                            0xFFDBFB98.toInt(),
                            0xFFEEFFCC.toInt(),
                            0xFFA8E6CF.toInt(),
                            0xFFDCE775.toInt()
                        ),
                        emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(100),
                        position = Position.Relative(0.5, 0.0)
                    ),
                    Party(
                        speed = 25f,
                        maxSpeed = 45f,
                        damping = 0.9f,
                        angle = 315,
                        spread = 60,
                        colors = listOf(
                            0xFFBEDC7F.toInt(),
                            0xFF89A257.toInt(),
                            0xFFDBFB98.toInt(),
                            0xFFEEFFCC.toInt(),
                            0xFFA8E6CF.toInt()
                        ),
                        emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(80),
                        position = Position.Relative(0.0, 0.0)
                    ),
                    Party(
                        speed = 25f,
                        maxSpeed = 45f,
                        damping = 0.9f,
                        angle = 225,
                        spread = 60,
                        colors = listOf(
                            0xFFBEDC7F.toInt(),
                            0xFF89A257.toInt(),
                            0xFFDBFB98.toInt(),
                            0xFFEEFFCC.toInt(),
                            0xFFA8E6CF.toInt()
                        ),
                        emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(80),
                        position = Position.Relative(1.0, 0.0)
                    ),
                    Party(
                        speed = 20f,
                        maxSpeed = 40f,
                        damping = 0.9f,
                        angle = 45,
                        spread = 45,
                        colors = listOf(
                            0xFFBEDC7F.toInt(),
                            0xFF89A257.toInt(),
                            0xFFDBFB98.toInt(),
                            0xFFEEFFCC.toInt()
                        ),
                        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(60),
                        position = Position.Relative(0.0, 1.0)
                    ),
                    Party(
                        speed = 20f,
                        maxSpeed = 40f,
                        damping = 0.9f,
                        angle = 135,
                        spread = 45,
                        colors = listOf(
                            0xFFBEDC7F.toInt(),
                            0xFF89A257.toInt(),
                            0xFFDBFB98.toInt(),
                            0xFFEEFFCC.toInt()
                        ),
                        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(60),
                        position = Position.Relative(1.0, 1.0)
                    )
                )
            )
        }
    }
}

@Composable
private fun GlowingScoreCircle(
    score: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(
                    color = PrimaryGreenLightAlpha10,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(210.dp)
                .background(
                    color = PrimaryGreenLightAlpha30,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    color = PrimaryGreen,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.your_score),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = PastelYellow
                )

                Text(
                    text = score.toString(),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PastelYellow
                )
            }
        }
    }
}

@Composable
private fun QuizInfoBox(
    completionPercentage: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    wrongAnswers: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                InfoItem(
                    count = "$completionPercentage%",
                    label = stringResource(R.string.completion),
                    color = MediumGreenMint
                )
                InfoItem(
                    count = correctAnswers.toString(),
                    label = stringResource(R.string.correct),
                    color = PrimaryGreenNeon
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                InfoItem(
                    count = totalQuestions.toString(),
                    label = stringResource(R.string.total_questions),
                    color = MediumGreenPale
                )
                InfoItem(
                    count = wrongAnswers.toString(),
                    label = stringResource(R.string.wrong),
                    color = ErrorRedDark
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    color: Color = White,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Column {
            Text(
                text = count,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PastelYellow
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizResultPreview() {
    FaunaDexTheme {
        QuizResultScreen()
    }
}