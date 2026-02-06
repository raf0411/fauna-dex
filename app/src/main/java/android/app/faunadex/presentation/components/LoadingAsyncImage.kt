package android.app.faunadex.presentation.components

import android.app.faunadex.R
import androidx.compose.foundation.Image
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
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale
    ) {
        val state = painter.state
        when (state) {
            is coil.compose.AsyncImagePainter.State.Loading,
            is coil.compose.AsyncImagePainter.State.Empty -> {
                ShimmerEffect(
                    modifier = Modifier.matchParentSize(),
                    shape = shape
                )
            }
            is coil.compose.AsyncImagePainter.State.Success -> {
                SubcomposeAsyncImageContent()
            }
            is coil.compose.AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(error),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}
