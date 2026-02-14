package android.app.faunadex.utils

import android.content.Context
import android.util.Log

object PermissionsManager {

    private const val TAG = "PermissionsManager"
    private const val PREFS_NAME = "faunadex_permissions_prefs"
    private const val KEY_PERMISSIONS_REQUESTED = "permissions_requested"

    /**
     * Check if permissions have been requested before
     */
    fun hasRequestedPermissions(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val hasRequested = prefs.getBoolean(KEY_PERMISSIONS_REQUESTED, false)
            Log.d(TAG, "hasRequestedPermissions: $hasRequested")
            hasRequested
        } catch (e: Exception) {
            Log.e(TAG, "Error checking permissions requested status", e)
            false
        }
    }

    /**
     * Mark that permissions have been requested
     */
    fun setPermissionsRequested(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_PERMISSIONS_REQUESTED, true).apply()
            Log.d(TAG, "Permissions requested status set to true")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting permissions requested status", e)
        }
    }

    /**
     * Reset permissions requested status (for testing purposes)
     */
    fun resetPermissionsRequested(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_PERMISSIONS_REQUESTED, false).apply()
            Log.d(TAG, "Permissions requested status reset to false")
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting permissions requested status", e)
        }
    }
}

