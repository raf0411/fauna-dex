package android.app.faunadex.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes

class QuizMusicPlayer(
    private val context: Context,
    @RawRes private val musicResId: Int
) {
    private var mediaPlayer: MediaPlayer? = null
    private var isInitialized = false

    fun play() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, musicResId)?.apply {
                    isLooping = true
                    setVolume(0.4f, 0.4f) // Set to 40% volume for background music
                    isInitialized = true
                }
            }

            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    Log.d("QuizMusicPlayer", "Music started")
                }
            }
        } catch (e: Exception) {
            Log.e("QuizMusicPlayer", "Error playing music: ${e.message}")
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    Log.d("QuizMusicPlayer", "Music paused")
                }
            }
        } catch (e: Exception) {
            Log.e("QuizMusicPlayer", "Error pausing music: ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                Log.d("QuizMusicPlayer", "Music stopped")
            }
        } catch (e: Exception) {
            Log.e("QuizMusicPlayer", "Error stopping music: ${e.message}")
        }
    }

    fun release() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                Log.d("QuizMusicPlayer", "Music player released")
            }
            mediaPlayer = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("QuizMusicPlayer", "Error releasing music player: ${e.message}")
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (e: Exception) {
            false
        }
    }
}
