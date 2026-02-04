package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
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
                        Image(
                            painter = painterResource(id = R.drawable.warning_icon),
                            contentDescription = "Warning",
                            modifier = Modifier.size(98.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = title,
                            fontFamily = JerseyFont,
                            fontSize = 32.sp,
                            color = PastelYellow,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = message,
                            fontFamily = JerseyFont,
                            fontSize = 18.sp,
                            color = MediumGreenSage,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PastelYellow
                                ),
                                border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = cancelText,
                                    fontFamily = JerseyFont,
                                    fontSize = 20.sp,
                                    color = PastelYellow
                                )
                            }

                            Button(
                                onClick = {
                                    onConfirm()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = confirmText,
                                    fontFamily = JerseyFont,
                                    fontSize = 20.sp,
                                    color = PastelYellow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    FaunaDexTheme {
        ConfirmationDialog(
            title = "Title",
            message = "Lorem ipsum is a dummy or placeholder text commonly used?",
            onConfirm = {},
            onDismiss = {},
            showDialog = true
        )
    }
}