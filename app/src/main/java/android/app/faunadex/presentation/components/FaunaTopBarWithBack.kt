package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaunaTopBarWithBack(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    fontFamily = JerseyFont,
                    fontSize = 24.sp,
                    color = PrimaryGreenLight,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(40.dp)
                    .background(
                        color = PrimaryGreenAlpha60,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = PrimaryGreenLight,
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkGreen
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FaunaTopBarWithBackPreview() {
    FaunaDexTheme {
        FaunaTopBarWithBack(
            title = "Title",
            onNavigateBack = {}
        )
    }
}