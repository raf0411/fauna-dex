package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.DarkGreenMoss
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.QuizGreenGradient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizGameplayScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = stringResource(R.string.quiz_gameplay),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        QuizGameplayContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun QuizGameplayContent(
    modifier: Modifier = Modifier
) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            QuestionBox()

            Spacer(Modifier.height(96.dp))

            AnswerOptionsList(
                answers = listOf(
                    "Varanus komodoensis",
                    "Panthera tigris",
                    "Elephas maximus",
                    "Rhinoceros sondaicus"
                ),
                selectedAnswer = selectedAnswer,
                onAnswerSelected = { index ->
                    selectedAnswer = index
                }
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
                        append("Question ")
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
                onClick = { onAnswerSelected(index) }
            )
        }
    }
}

@Composable
fun AnswerOption(
    answer: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = DarkGreen,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) DarkGreenMoss else PrimaryGreenLime
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
                        color = if (isSelected) DarkGreenMoss else androidx.compose.ui.graphics.Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = if (isSelected) androidx.compose.ui.graphics.Color.Transparent else DarkForest,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = PrimaryGreenLight,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
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

@Preview(showBackground = true)
@Composable
fun QuizGameplayScreenPreview() {
    FaunaDexTheme {
        QuizGameplayScreen()
    }
}