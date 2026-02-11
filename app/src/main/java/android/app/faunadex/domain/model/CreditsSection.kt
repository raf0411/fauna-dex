package android.app.faunadex.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CreditsSection(
    val titleResId: Int,
    val icon: ImageVector,
    val descriptionResId: Int? = null,
    val items: List<CreditItem>
)