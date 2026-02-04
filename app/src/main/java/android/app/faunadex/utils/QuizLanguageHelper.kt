package android.app.faunadex.utils

import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.content.Context

object QuizLanguageHelper {

    fun getQuizTitle(quiz: Quiz, context: Context): String {
        val language = LanguageManager.getLanguage(context)
        return quiz.getTitle(language)
    }

    fun getQuizDescription(quiz: Quiz, context: Context): String {
        val language = LanguageManager.getLanguage(context)
        return quiz.getDescription(language)
    }

    fun getQuestionText(question: Question, context: Context): String {
        val language = LanguageManager.getLanguage(context)
        return question.getQuestionText(language)
    }

    fun getQuestionOptions(question: Question, context: Context): List<String> {
        val language = LanguageManager.getLanguage(context)
        return question.getOptions(language)
    }

    fun getQuestionExplanation(question: Question, context: Context): String {
        val language = LanguageManager.getLanguage(context)
        return question.getExplanation(language)
    }
}
