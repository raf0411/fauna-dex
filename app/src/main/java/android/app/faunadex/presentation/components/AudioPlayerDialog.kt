package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.utils.AudioPlaybackState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerDialog(
    animalName: String,
    playbackState: AudioPlaybackState,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = DarkForest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with animal name and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Audio Narration",
                            fontFamily = PoppinsFont,
                            fontSize = 14.sp,
                            color = MediumGreenSage
                        )
                        Text(
                            text = animalName,
                            fontFamily = PoppinsFont,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PastelYellow
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = MediumGreenSage
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Content based on playback state
                when (playbackState) {
                    is AudioPlaybackState.ERROR -> {
                        AudioErrorContent(message = playbackState.message)
                    }
                    AudioPlaybackState.IDLE -> {
                        AudioIdleContent()
                    }
                    AudioPlaybackState.LOADING -> {
                        AudioLoadingContent()
                    }
                    else -> {
                        AudioPlayingContent(
                            isPlaying = playbackState == AudioPlaybackState.PLAYING,
                            currentPosition = currentPosition,
                            duration = duration,
                            onPlayPauseClick = onPlayPauseClick
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AudioIdleContent() {
    Column(
        modifier = Modifier.padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Press play to start audio narration",
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            color = MediumGreenSage,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AudioLoadingContent() {
    Column(
        modifier = Modifier.padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = PastelYellow,
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Loading audio...",
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            color = MediumGreenSage
        )
    }
}

@Composable
private fun AudioErrorContent(message: String) {
    Column(
        modifier = Modifier.padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            fontSize = 48.sp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Audio Error",
            fontFamily = PoppinsFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B6B)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            color = MediumGreenSage,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AudioPlayingContent(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Waveform animation when playing
        if (isPlaying) {
            AudioWaveformAnimation()
            Spacer(Modifier.height(32.dp))
        } else {
            Spacer(Modifier.height(80.dp))
        }

        // Play/Pause Button
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PastelYellow)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = DarkForest,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Progress and Time
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    fontFamily = PoppinsFont,
                    fontSize = 14.sp,
                    color = MediumGreenSage
                )
                Text(
                    text = formatTime(duration),
                    fontFamily = PoppinsFont,
                    fontSize = 14.sp,
                    color = MediumGreenSage
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PastelYellow,
                trackColor = MediumGreenSage.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
private fun AudioWaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(15) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        delayMillis = index * 40,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave$index"
            )

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .scale(scaleY = scale, scaleX = 1f)
                    .background(
                        color = PrimaryGreenLight.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(1.5.dp)
                    )
            )

            if (index < 14) {
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
