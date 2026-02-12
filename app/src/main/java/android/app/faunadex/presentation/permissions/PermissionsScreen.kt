package android.app.faunadex.presentation.permissions

import android.Manifest
import android.app.faunadex.R
import android.app.faunadex.presentation.components.AuthButton
import android.app.faunadex.ui.theme.*
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import android.app.faunadex.utils.PermissionsManager

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    onPermissionsHandled: () -> Unit
) {
    val context = LocalContext.current

    val permissions = remember {
        buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    PermissionsScreenContent(
        permissionsState = permissionsState,
        onRequestPermissions = {
            permissionsState.launchMultiplePermissionRequest()
        },
        onContinue = {
            PermissionsManager.setPermissionsRequested(context)
            onPermissionsHandled()
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreenContent(
    permissionsState: MultiplePermissionsState,
    onRequestPermissions: () -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkForest
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.permissions_title),
                fontFamily = JerseyFont,
                fontSize = 36.sp,
                color = PastelYellow,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.permissions_description),
                fontFamily = PoppinsFont,
                fontSize = 15.sp,
                color = MediumGreenSage,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            PermissionCard(
                icon = Icons.Default.CameraAlt,
                title = stringResource(R.string.permission_camera_title),
                description = stringResource(R.string.permission_camera_description),
                isGranted = permissionsState.permissions.any {
                    it.permission == Manifest.permission.CAMERA && it.status.isGranted
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionCard(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.permission_location_title),
                description = stringResource(R.string.permission_location_description),
                isGranted = permissionsState.permissions.any {
                    (it.permission == Manifest.permission.ACCESS_FINE_LOCATION ||
                     it.permission == Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    it.status.isGranted
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionCard(
                icon = Icons.Default.Image,
                title = stringResource(R.string.permission_storage_title),
                description = stringResource(R.string.permission_storage_description),
                isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsState.permissions.any {
                        it.permission == Manifest.permission.READ_MEDIA_IMAGES && it.status.isGranted
                    }
                } else {
                    permissionsState.permissions.any {
                        it.permission == Manifest.permission.READ_EXTERNAL_STORAGE && it.status.isGranted
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!permissionsState.allPermissionsGranted) {
                AuthButton(
                    text = stringResource(R.string.permissions_grant_button),
                    onClick = onRequestPermissions,
                    baseColor = PrimaryGreenLight,
                    shineColor = PrimaryGreenLime,
                    shadeColor = PrimaryGreenLime,
                    textColor = White,
                    strokeWidth = 14f,
                    shineHeight = 32.dp,
                    height = 80.dp,
                    fontSize = 28.sp,
                    fontFamily = CodeNextFont,
                    cornerRadius = 28.dp,
                    shadowElevation = 12.dp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (permissionsState.allPermissionsGranted ||
                permissionsState.permissions.any { !it.status.isGranted }) {

                TextButton(
                    onClick = onContinue,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (permissionsState.allPermissionsGranted) {
                            stringResource(R.string.permissions_continue_button)
                        } else {
                            stringResource(R.string.permissions_skip_button)
                        },
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        color = PrimaryGreenLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.permissions_note),
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = DarkGreenMoss
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isGranted) PrimaryGreen else DarkGreenTeal,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) White else MediumGreenSage,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontFamily = PoppinsFont,
                    fontSize = 16.sp,
                    color = PastelYellow,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontFamily = PoppinsFont,
                    fontSize = 13.sp,
                    color = MediumGreenSage,
                    lineHeight = 18.sp
                )
            }

            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = PrimaryGreenLight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

