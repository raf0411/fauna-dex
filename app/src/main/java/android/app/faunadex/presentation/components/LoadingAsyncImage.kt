package android.app.faunadex.presentation.components

import android.app.faunadex.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

/**
 * Custom AsyncImage with shimmer loading effect
 * Shows skeleton animation while image is loading
 * Best practice: Provides better UX by showing loading state
 */
@Composable
fun LoadingAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(0.dp),
    placeholder: Int = R.drawable.animal_dummy,
    error: Int = R.drawable.animal_dummy
) {
    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .clip(shape),
            contentScale = contentScale
        ) {
            val state = painter.state
            if (state is coil.compose.AsyncImagePainter.State.Loading ||
                state is coil.compose.AsyncImagePainter.State.Empty) {
                ShimmerEffect(
                    modifier = Modifier.matchParentSize(),
                    shape = shape
                )
            } else {
                SubcomposeAsyncImageContent()
            }
        }
    }
}
