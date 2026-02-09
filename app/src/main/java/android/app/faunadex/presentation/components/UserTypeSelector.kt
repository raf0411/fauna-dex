package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.DarkGreenMoss
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreenLime
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserTypeOption(
    val type: String,
    val labelResId: Int,
    val icon: ImageVector
)

@Composable
fun UserTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val userTypes = listOf(
        UserTypeOption(
            type = "Student",
            labelResId = R.string.user_type_student,
            icon = Icons.Outlined.School
        ),
        UserTypeOption(
            type = "Teacher",
            labelResId = R.string.user_type_teacher,
            icon = Icons.Outlined.WorkOutline
        )
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        userTypes.forEachIndexed { index, userType ->
            UserTypeOptionItem(
                userType = userType,
                isSelected = selectedType == userType.type,
                onClick = { onTypeSelected(userType.type) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            if (index < userTypes.size - 1) {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
private fun UserTypeOptionItem(
    userType: UserTypeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) PrimaryGreenLime.copy(alpha = 0.3f)
                else DarkGreenMoss
            )
            .border(
                width = 3.dp,
                color = if (isSelected) PastelYellow else AlmostBlack,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 16.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = userType.icon,
                contentDescription = stringResource(userType.labelResId),
                tint = if (isSelected) PastelYellow else MediumGreenSage,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(userType.labelResId),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PastelYellow else MediumGreenSage,
                fontFamily = JerseyFont
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserTypeSelectorPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var selectedType by remember { mutableStateOf("Student") }

                Text(
                    text = "Selected: $selectedType",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                UserTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserTypeSelectorTeacherPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            UserTypeSelector(
                selectedType = "Teacher",
                onTypeSelected = {}
            )
        }
    }
}

