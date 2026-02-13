package android.app.faunadex.presentation.credits

import android.app.faunadex.R
import android.app.faunadex.domain.model.CreditItem
import android.app.faunadex.domain.model.CreditsSection
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.*
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlin.collections.forEachIndexed

@Composable
fun CreditsScreen(
    onNavigateBack: () -> Unit = {}
) {
    CreditsScreenContent(
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CreditsScreenContent(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = stringResource(R.string.credits_title),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AppInfoCard()
            }

//            item {
//                CreditsCard(
//                    section = CreditsSection(
//                        titleResId = R.string.credits_ai_models_title,
//                        icon = Icons.Outlined.ViewInAr,
//                        descriptionResId = R.string.credits_ai_models_description,
//                        items = listOf(
//                            CreditItem(
//                                titleResId = R.string.credits_meshy_ai,
//                                descriptionResId = R.string.credits_meshy_ai_description,
//                                license = "Public License (Pro)",
//                                url = "https://meshy.ai/"
//                            )
//                        )
//                    ),
//                    onUrlClick = { url ->
//                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
//                        context.startActivity(intent)
//                    }
//                )
//            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_external_models_title,
                        icon = Icons.Outlined.Widgets,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_meshy_ai,
                                descriptionResId = R.string.credits_meshy_ai_description,
                                license = "Commercial Use License (Pro)",
                                url = "https://www.meshy.ai/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_sketchfab_models,
                                author = "Various Artists",
                                license = "CC BY 4.0 / Royalty Free",
                                url = "https://sketchfab.com/"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_images_title,
                        icon = Icons.Outlined.Image,
                        descriptionResId = R.string.credits_images_description,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_tim_laman,
                                descriptionResId = R.string.credits_tim_laman_desc,
                                license = "CC BY 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_charles_hardin,
                                descriptionResId = R.string.credits_charles_hardin_desc,
                                license = "CC BY 2.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_vaclav_silha,
                                descriptionResId = R.string.credits_vaclav_silha_desc,
                                license = "CC BY-SA 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_seshadri_ks,
                                descriptionResId = R.string.credits_seshadri_ks_desc,
                                license = "CC BY-SA 3.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_stefan_brending,
                                descriptionResId = R.string.credits_stefan_brending_desc,
                                license = "CC BY-SA 3.0 DE",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_mangkau_zulkifli,
                                descriptionResId = R.string.credits_mangkau_zulkifli_desc,
                                license = "CC BY-SA 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_dick_daniels,
                                descriptionResId = R.string.credits_dick_daniels_desc,
                                license = "CC BY-SA 3.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_jj_harrison,
                                descriptionResId = R.string.credits_jj_harrison_desc,
                                license = "CC BY-SA 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_fabian_lambeck,
                                descriptionResId = R.string.credits_fabian_lambeck_desc,
                                license = "CC BY-SA 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_julien_willem,
                                descriptionResId = R.string.credits_julien_willem_desc,
                                license = "CC BY-SA 3.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_james_jolokia,
                                descriptionResId = R.string.credits_james_jolokia_desc,
                                license = "CC BY 4.0",
                                url = "https://www.inaturalist.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_vardhan_patankar,
                                descriptionResId = R.string.credits_vardhan_patankar_desc,
                                license = "CC BY 4.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_derek_keats,
                                descriptionResId = R.string.credits_derek_keats_desc,
                                license = "CC BY 2.0",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_arturo_frias,
                                descriptionResId = R.string.credits_arturo_frias_desc,
                                license = "CC BY-SA",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_wikimedia_commons,
                                descriptionResId = R.string.credits_wikimedia_commons_desc,
                                license = "Various CC Licenses",
                                url = "https://commons.wikimedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_inaturalist_images,
                                descriptionResId = R.string.credits_inaturalist_images_desc,
                                license = "CC BY 4.0",
                                url = "https://www.inaturalist.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_animalia_bio,
                                descriptionResId = R.string.credits_animalia_bio_desc,
                                license = "Nonprofit Attribution",
                                url = "https://animalia.bio/"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_music_title,
                        icon = Icons.Outlined.MusicNote,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_ievgen_poltavskyi,
                                descriptionResId = R.string.credits_ievgen_poltavskyi_desc,
                                license = "Pixabay Content License",
                                url = "https://pixabay.com/music/id-295075/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_pixabay_music,
                                descriptionResId = R.string.credits_pixabay_music_desc,
                                license = "Pixabay Content License",
                                url = "https://pixabay.com/music/"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_scientific_sources_title,
                        icon = Icons.Outlined.School,
                        descriptionResId = R.string.credits_scientific_sources_description,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_inaturalist,
                                descriptionResId = R.string.credits_inaturalist_desc,
                                license = "CC BY-NC 4.0",
                                url = "https://www.inaturalist.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_wikipedia,
                                descriptionResId = R.string.credits_wikipedia_desc,
                                license = "CC BY-SA 3.0",
                                url = "https://www.wikipedia.org/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_youtube,
                                descriptionResId = R.string.credits_youtube_desc,
                                license = "Standard YouTube License",
                                url = "https://www.youtube.com/"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_libraries_title,
                        icon = Icons.Outlined.Code,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_google_arcore,
                                descriptionResId = R.string.credits_google_arcore_desc,
                                license = "Apache 2.0",
                                url = "https://developers.google.com/ar"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_jetpack_compose,
                                descriptionResId = R.string.credits_jetpack_compose_desc,
                                license = "Apache 2.0",
                                url = "https://developer.android.com/jetpack/compose"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_coil,
                                descriptionResId = R.string.credits_coil_desc,
                                license = "Apache 2.0",
                                url = "https://coil-kt.github.io/coil/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_hilt,
                                descriptionResId = R.string.credits_hilt_desc,
                                license = "Apache 2.0",
                                url = "https://dagger.dev/hilt/"
                            ),
                            CreditItem(
                                titleResId = R.string.credits_firebase,
                                descriptionResId = R.string.credits_firebase_desc,
                                license = "Apache 2.0",
                                url = "https://firebase.google.com/"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                CreditsCard(
                    section = CreditsSection(
                        titleResId = R.string.credits_icons_title,
                        icon = Icons.Outlined.Palette,
                        items = listOf(
                            CreditItem(
                                titleResId = R.string.credits_material_icons,
                                license = "Apache 2.0",
                                url = "https://fonts.google.com/icons"
                            )
                        )
                    ),
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                )
            }

//            item {
//                Button(
//                    onClick = onNavigateToOpenSourceLicenses,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = PrimaryGreen,
//                        contentColor = PastelYellow
//                    ),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.Description,
//                        contentDescription = null,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = stringResource(R.string.credits_view_licenses),
//                        fontFamily = JerseyFont,
//                        fontSize = 18.sp
//                    )
//                }
//            }

            item {
                AcademicDisclaimerCard()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun AppInfoCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = DarkGreenMoss
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PrimaryGreenLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.credits_app_name),
                fontFamily = JerseyFont,
                fontSize = 32.sp,
                color = PastelYellow,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.credits_developer),
                fontFamily = PoppinsFont,
                fontSize = 16.sp,
                color = MediumGreenSage,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.credits_year),
                fontFamily = PoppinsFont,
                fontSize = 14.sp,
                color = MediumGreenSage,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = DarkGreenTeal,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.credits_copyright),
                fontFamily = PoppinsFont,
                fontSize = 14.sp,
                color = PrimaryGreenLight,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.credits_purpose),
                fontFamily = PoppinsFont,
                fontSize = 13.sp,
                color = MediumGreenSage,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun CreditsCard(
    section: CreditsSection,
    onUrlClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = DarkGreenMoss
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint = PrimaryGreenLight,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(section.titleResId),
                    fontFamily = JerseyFont,
                    fontSize = 22.sp,
                    color = PastelYellow,
                    fontWeight = FontWeight.Bold
                )
            }

            section.descriptionResId?.let { descId ->
                Text(
                    text = stringResource(descId),
                    fontFamily = PoppinsFont,
                    fontSize = 13.sp,
                    color = MediumGreenSage,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            section.items.forEachIndexed { index, item ->
                CreditItemRow(
                    item = item,
                    onUrlClick = onUrlClick
                )
                if (index < section.items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = DarkGreenTeal,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun CreditItemRow(
    item: CreditItem,
    onUrlClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(item.titleResId),
            fontFamily = PoppinsFont,
            fontSize = 15.sp,
            color = PrimaryGreenLight,
            fontWeight = FontWeight.SemiBold
        )

        item.descriptionResId?.let { descId ->
            Text(
                text = stringResource(descId),
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item.author?.let { author ->
            Text(
                text = stringResource(R.string.credits_author_format, author),
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item.license?.let { license ->
            Text(
                text = stringResource(R.string.credits_license_format, license),
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item.url?.let { url ->
            TextButton(
                onClick = { onUrlClick(url) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.credits_visit_website),
                    fontFamily = PoppinsFont,
                    fontSize = 13.sp,
                    color = BlueOcean,
                    textDecoration = TextDecoration.Underline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = BlueOcean
                )
            }
        }
    }
}

@Composable
private fun AcademicDisclaimerCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = DarkGreenTeal
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = PastelYellow
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.credits_academic_disclaimer_title),
                fontFamily = JerseyFont,
                fontSize = 18.sp,
                color = PastelYellow,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.credits_academic_disclaimer),
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenPale,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreditsScreenPreview() {
    FaunaDexTheme {
        CreditsScreenContent(
            onNavigateBack = {}
        )
    }
}

