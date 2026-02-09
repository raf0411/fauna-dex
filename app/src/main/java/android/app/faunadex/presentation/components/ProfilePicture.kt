package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.*
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProfilePicture(
    imageUrl: String? = null,
    imagePainter: Painter? = null,
    username: String = "",
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    borderWidth: Dp = 8.dp,
    onEditClick: () -> Unit
) {
    Box(
        modifier = modifier.size(size)
    ) {
        val painter = remember(imageUrl) {
            if (!imageUrl.isNullOrEmpty() && imageUrl.startsWith("data:image")) {
                try {
                    val base64Data = imageUrl.substringAfter("base64,")
                    val imageBytes = Base64.decode(base64Data, Base64.NO_WRAP)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    BitmapPainter(bitmap.asImageBitmap())
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }
        }

        when {
            painter != null -> {
                Image(
                    painter = painter,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(borderWidth, Color.White, CircleShape)
                )
            }
            !imageUrl.isNullOrEmpty() && !imageUrl.startsWith("data:image") -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    placeholder = if (username.isNotEmpty()) null else painterResource(id = R.drawable.dummy_profile),
                    error = if (username.isNotEmpty()) null else painterResource(id = R.drawable.dummy_profile),
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(borderWidth, Color.White, CircleShape)
                )
            }
            imagePainter != null -> {
                Image(
                    painter = imagePainter,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(borderWidth, Color.White, CircleShape)
                )
            }
            username.isNotEmpty() -> {
                ProfileInitialsLarge(
                    username = username,
                    size = size,
                    borderWidth = borderWidth
                )
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.dummy_profile),
                    contentDescription = "Default Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(borderWidth, Color.White, CircleShape)
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = MediumGreen,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(size / 3.5f)
                .border(2.dp, Color.White, CircleShape)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable(onClick = onEditClick)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile Picture",
                    tint = PrimaryGreenLight,
                    modifier = Modifier
                        .padding(6.dp)
                )
            }
        }
    }
}

private fun getUserInitials(username: String): String {
    val cleanUsername = username
        .replace(Regex("[^a-zA-Z0-9]"), " ")
        .trim()

    return when {
        cleanUsername.isEmpty() -> "??"
        cleanUsername.contains(" ") -> {
            val words = cleanUsername.split(" ").filter { it.isNotEmpty() }
            if (words.size >= 2) {
                "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            } else {
                words[0].take(2).uppercase()
            }
        }
        cleanUsername.length >= 2 -> {
            cleanUsername.take(2).uppercase()
        }
        else -> {
            cleanUsername.uppercase().repeat(2)
        }
    }
}

@Composable
private fun ProfileInitialsLarge(
    username: String,
    size: Dp,
    borderWidth: Dp,
    modifier: Modifier = Modifier
) {
    val initials = getUserInitials(username)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MediumGreenSage)
            .border(borderWidth, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size.value / 3).sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = DarkGreenMoss,
            fontFamily = JerseyFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePicturePreview() {
    FaunaDexTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
            ProfilePicture(
                imagePainter = painterResource(id = R.drawable.dummy_profile),
                onEditClick = { println("Edit Clicked") }
            )
        }
    }
}