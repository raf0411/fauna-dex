package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.MediumGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

@Composable
fun ProfilePicture(
    imageUrl: String? = null,
    imagePainter: Painter? = null,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    borderWidth: Dp = 8.dp,
    onEditClick: () -> Unit
) {
    Box(
        modifier = modifier.size(size)
    ) {
        // Check if imageUrl is a Base64 data URL
        val painter = remember(imageUrl) {
            if (!imageUrl.isNullOrEmpty() && imageUrl.startsWith("data:image")) {
                try {
                    // Extract Base64 data from data URL
                    val base64Data = imageUrl.substringAfter("base64,")
                    val imageBytes = Base64.decode(base64Data, Base64.NO_WRAP)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    BitmapPainter(bitmap.asImageBitmap())
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

        when {
            painter != null -> {
                // Display Base64 decoded image
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
                // Display URL image using Coil
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.dummy_profile),
                    error = painterResource(id = R.drawable.dummy_profile),
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(borderWidth, Color.White, CircleShape)
                )
            }
            imagePainter != null -> {
                // Display provided painter
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
            else -> {
                // Display default dummy profile
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