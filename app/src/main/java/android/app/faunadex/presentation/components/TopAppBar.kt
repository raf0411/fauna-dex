package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.util.*

data class TopAppBarUserData(
    val username: String,
    val profilePictureUrl: String? = null,
    val educationLevel: String,
    val userType: String = "Student",
    val currentLevel: Int,
    val currentXp: Int,
    val xpForNextLevel: Int
) {
    fun getInitials(): String {
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
                // Single character: duplicate it
                cleanUsername.uppercase().repeat(2)
            }
        }
    }
}

@Composable
fun TopAppBar(
    userData: TopAppBarUserData,
    modifier: Modifier = Modifier,
    showProfilePicture: Boolean = true,
    showEducationBadge: Boolean = true,
    showLevelAndProgress: Boolean = true
) {
    val greeting = getGreeting()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val educationLevelLabel = stringResource(R.string.education_level_label)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .padding(top = statusBarPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                if (showProfilePicture) {
                    ProfilePicture(
                        profilePictureUrl = userData.profilePictureUrl,
                        username = userData.username,
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = "$greeting!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = MediumGreenSage,
                        fontFamily = JerseyFont
                    )

                    Text(
                        text = userData.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelYellow,
                        fontFamily = JerseyFont
                    )

                    if (showEducationBadge) {
                        Spacer(Modifier.height(8.dp))

                        if (userData.userType == "Teacher") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TeacherBadgeCompact()
                                EducationLevelBadgeCompact(
                                    educationLevel = userData.educationLevel
                                )
                            }
                        } else {
                            EducationLevelBadgeWithLabel(
                                educationLevel = userData.educationLevel,
                                educationLevelLabel = educationLevelLabel
                            )
                        }
                    }
                }
            }

            if (showLevelAndProgress) {
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HexagonLevel(
                        level = userData.currentLevel
                    )

                    XpProgress(
                        currentXp = userData.currentXp,
                        maxXp = userData.xpForNextLevel,
                        progressColor = PrimaryGreenLight,
                        backgroundColor = DarkGreenShade
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePicture(
    profilePictureUrl: String?,
    username: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MediumGreenSage)
            .border(2.dp, White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUrl != null && profilePictureUrl.isNotBlank()) {
            val imageData = if (profilePictureUrl.startsWith("data:image")) {
                val base64Data = profilePictureUrl.substringAfter("base64,")
                android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            } else {
                profilePictureUrl
            }

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageData)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    UserInitials(username = username)
                },
                error = {
                    UserInitials(username = username)
                }
            )
        } else {
            UserInitials(username = username)
        }
    }
}

@Composable
private fun UserInitials(
    username: String,
    modifier: Modifier = Modifier
) {
    val initials = TopAppBarUserData(
        username = username,
        educationLevel = "",
        currentLevel = 0,
        currentXp = 0,
        xpForNextLevel = 0
    ).getInitials()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreenMoss,
            fontFamily = JerseyFont
        )
    }
}

@Composable
private fun EducationLevelBadgeCompact(
    educationLevel: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (educationLevel) {
        "SD" -> ErrorRed
        "SMP" -> PrimaryBlue
        "SMA" -> BlueOcean
        else -> BlueOcean
    }

    Box(
        modifier = modifier
            .background(badgeColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = educationLevel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
    }
}

@Composable
private fun EducationLevelBadgeWithLabel(
    educationLevel: String,
    educationLevelLabel: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (educationLevel) {
        "SD" -> ErrorRed
        "SMP" -> PrimaryBlue
        "SMA" -> BlueOcean
        else -> BlueOcean
    }

    Row(
        modifier = modifier
            .background(badgeColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = educationLevelLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
        Text(
            text = educationLevel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
    }
}

@Composable
private fun TeacherBadgeCompact(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(PastelYellow, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.user_type_teacher),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreenMoss,
                fontFamily = JerseyFont
            )
        }
    }
}

@Composable
private fun HexagonLevel(
    level: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .offset(x = 3.dp, y = 4.dp)
                .background(Black.copy(alpha = 0.6f), HexagonShape())
        )

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(PrimaryGreenLight, HexagonShape())
                .border(6.dp, DarkGreenTeal, HexagonShape()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreenMoss,
                fontFamily = JerseyFont
            )
        }
    }
}

@Composable
private fun XpProgress(
    currentXp: Int,
    maxXp: Int,
    progressColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maxXp > 0) currentXp.toFloat() / maxXp.toFloat() else 0f

    var animate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animate = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 300
        ),
        label = "xpProgressAnimation"
    )

    Column(
        modifier = modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = PastelYellow.copy(alpha = 0.8f),
                fontFamily = JerseyFont
            )
            Text(
                text = "$currentXp/$maxXp",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PastelYellow.copy(alpha = 0.7f),
                fontFamily = JerseyFont
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(progressColor)
            )
        }
    }
}


@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> stringResource(R.string.good_morning)
        in 12..17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_night)
    }
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    FaunaDexTheme {
        TopAppBar(
            userData = TopAppBarUserData(
                username = "raf_0411",
                profilePictureUrl = null,
                educationLevel = "SMA",
                currentLevel = 5,
                currentXp = 450,
                xpForNextLevel = 1000
            )
        )
    }
}