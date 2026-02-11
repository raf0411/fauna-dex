package android.app.faunadex.domain.model

data class CreditItem(
    val titleResId: Int,
    val descriptionResId: Int? = null,
    val author: String? = null,
    val license: String? = null,
    val url: String? = null
)