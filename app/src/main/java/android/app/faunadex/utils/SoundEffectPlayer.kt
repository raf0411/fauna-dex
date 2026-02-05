package android.app.faunadex.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes

class SoundEffectPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun play(@RawRes soundResId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, soundResId)?.apply {
                setVolume(0.6f, 0.6f)
                setOnCompletionListener {
                    it.release()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("SoundEffectPlayer", "Error playing sound: ${e.message}")
        }
    }

    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("SoundEffectPlayer", "Error releasing sound player: ${e.message}")
        }
    }
}
