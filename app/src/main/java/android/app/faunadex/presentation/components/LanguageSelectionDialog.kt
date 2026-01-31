package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.utils.LanguageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    isLoading: Boolean = false,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = !isLoading,
                dismissOnClickOutside = !isLoading,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkForest
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.select_language),
                            fontFamily = JerseyFont,
                            fontSize = 28.sp,
                            color = PastelYellow,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = PrimaryGreenLight,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.loading),
                                fontFamily = JerseyFont,
                                fontSize = 18.sp,
                                color = MediumGreenSage
                            )
                        } else {
                            LanguageOption(
                                languageName = stringResource(R.string.english),
                                languageCode = LanguageManager.LANGUAGE_ENGLISH,
                                isSelected = currentLanguage == LanguageManager.LANGUAGE_ENGLISH,
                                onClick = { onLanguageSelected(LanguageManager.LANGUAGE_ENGLISH) }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LanguageOption(
                                languageName = stringResource(R.string.indonesian),
                                languageCode = LanguageManager.LANGUAGE_INDONESIAN,
                                isSelected = currentLanguage == LanguageManager.LANGUAGE_INDONESIAN,
                                onClick = { onLanguageSelected(LanguageManager.LANGUAGE_INDONESIAN) }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    languageName: String,
    languageCode: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) PrimaryGreen.copy(alpha = 0.3f) else Color.Transparent
            )
            .border(
                width = 2.dp,
                color = if (isSelected) PrimaryGreenLight else MediumGreenSage.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (languageCode == LanguageManager.LANGUAGE_ENGLISH) "🇬🇧" else "🇮🇩",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = languageName,
                fontFamily = JerseyFont,
                fontSize = 22.sp,
                color = if (isSelected) PastelYellow else MediumGreenSage
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) PrimaryGreenLight else MediumGreenSage.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreenLight)
                )
            }
        }
    }
}
