package android.app.faunadex.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.MessageDigest

/**
 * Utility class for caching 3D models locally to avoid re-downloading them every time.
 * Models are stored in the app's cache directory with a hash of the URL as the filename.
 */
object ModelCache {
    private const val TAG = "ModelCache"
    private const val CACHE_DIR = "ar_models"

    /**
     * Get the cached model file path, downloading it if not cached.
     * Returns the local file path that can be used with SceneView's ModelLoader.
     *
     * @param context Android context
     * @param modelUrl The remote URL of the GLB model
     * @return Local file path of the cached model, or null if download failed
     */
    suspend fun getCachedModelPath(context: Context, modelUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val cacheDir = File(context.cacheDir, CACHE_DIR)
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }

                // Create a unique filename based on URL hash
                val fileName = hashUrl(modelUrl) + ".glb"
                val cachedFile = File(cacheDir, fileName)

                // Check if already cached
                if (cachedFile.exists() && cachedFile.length() > 0) {
                    Log.d(TAG, "Model found in cache: ${cachedFile.absolutePath}")
                    return@withContext cachedFile.absolutePath
                }

                // Download the model
                Log.d(TAG, "Downloading model from: $modelUrl")
                val startTime = System.currentTimeMillis()

                val url = URL(modelUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 30000
                connection.readTimeout = 60000

                connection.getInputStream().use { input ->
                    cachedFile.outputStream().use { output ->
                        input.copyTo(output, bufferSize = 8192)
                    }
                }

                val downloadTime = System.currentTimeMillis() - startTime
                val fileSizeKB = cachedFile.length() / 1024
                Log.d(TAG, "Model downloaded in ${downloadTime}ms, size: ${fileSizeKB}KB, cached at: ${cachedFile.absolutePath}")

                cachedFile.absolutePath
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cache model: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Check if a model is already cached without downloading.
     */
    fun isModelCached(context: Context, modelUrl: String): Boolean {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        val fileName = hashUrl(modelUrl) + ".glb"
        val cachedFile = File(cacheDir, fileName)
        return cachedFile.exists() && cachedFile.length() > 0
    }

    /**
     * Get the cached file path without downloading (returns null if not cached).
     */
    fun getCachedFilePath(context: Context, modelUrl: String): String? {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        val fileName = hashUrl(modelUrl) + ".glb"
        val cachedFile = File(cacheDir, fileName)
        return if (cachedFile.exists() && cachedFile.length() > 0) {
            cachedFile.absolutePath
        } else {
            null
        }
    }

    /**
     * Clear all cached models.
     */
    fun clearCache(context: Context) {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (cacheDir.exists()) {
            cacheDir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "Model cache cleared")
        }
    }

    /**
     * Get total size of cached models in bytes.
     */
    fun getCacheSize(context: Context): Long {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        return if (cacheDir.exists()) {
            cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        } else {
            0L
        }
    }

    /**
     * Create a hash of the URL for use as a filename.
     */
    private fun hashUrl(url: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(url.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}

