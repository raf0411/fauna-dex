package android.app.faunadex.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import android.util.Log

object LanguageManager {

    private const val TAG = "LanguageManager"
    private const val PREFS_NAME = "faunadex_language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_SHOW_LANGUAGE_CHANGED_SNACKBAR = "show_language_changed_snackbar"

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_INDONESIAN = "id"

    fun getLanguage(context: Context): String {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lang = prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
            Log.d(TAG, "getLanguage: Retrieved language = $lang")
            lang
        } catch (e: Exception) {
            Log.e(TAG, "getLanguage: Error", e)
            LANGUAGE_ENGLISH
        }
    }

    fun setLanguage(context: Context, languageCode: String) {
        Log.d(TAG, "setLanguage: Saving language = $languageCode")
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).commit()
    }

    fun shouldShowLanguageChangedSnackbar(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_LANGUAGE_CHANGED_SNACKBAR, false)
    }

    fun clearLanguageChangedSnackbarFlag(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_LANGUAGE_CHANGED_SNACKBAR, false).apply()
    }

    fun setLanguageAndRestart(activity: Activity, languageCode: String) {
        Log.d(TAG, "setLanguageAndRestart: Setting language to $languageCode")

        setLanguage(activity.applicationContext, languageCode)

        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_LANGUAGE_CHANGED_SNACKBAR, true).commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "setLanguageAndRestart: Using LocaleManager (Android 13+)")
            try {
                val localeManager = activity.getSystemService(LocaleManager::class.java)
                localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
                Log.d(TAG, "setLanguageAndRestart: LocaleManager.applicationLocales set")
            } catch (e: Exception) {
                Log.e(TAG, "setLanguageAndRestart: LocaleManager failed", e)
            }
        }

        restartApp(activity)
    }

    private fun restartApp(activity: Activity) {
        Log.d(TAG, "restartApp: Restarting app...")

        val packageManager = activity.packageManager
        val intent = packageManager.getLaunchIntentForPackage(activity.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()

        Runtime.getRuntime().exit(0)
    }

    fun applySavedLocale(context: Context) {
        val languageCode = getLanguage(context)
        Log.d(TAG, "applySavedLocale: Applying saved language = $languageCode")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                val currentLocales = localeManager?.applicationLocales
                Log.d(TAG, "applySavedLocale: Current system locales = $currentLocales")

                if (currentLocales == null || currentLocales.isEmpty) {
                    localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
                    Log.d(TAG, "applySavedLocale: Set system locale to $languageCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "applySavedLocale: Error", e)
            }
        }
    }
}
