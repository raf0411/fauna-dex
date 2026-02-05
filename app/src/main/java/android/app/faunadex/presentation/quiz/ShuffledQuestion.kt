package android.app.faunadex.presentation.quiz

import android.app.faunadex.domain.model.Question

data class ShuffledQuestion(
    val originalQuestion: Question,
    val shuffledCorrectAnswerIndex: Int
)
