package android.app.faunadex.presentation.animalDetail

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.ErrorRed
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryBlue
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.app.faunadex.R
import android.app.faunadex.presentation.components.ConservationStatusBadge
import android.app.faunadex.presentation.components.EndemicStatusBadge
import android.app.faunadex.presentation.components.PopulationTrendBadge
import android.app.faunadex.presentation.components.ActivityPeriodBadge
import android.app.faunadex.presentation.components.ProtectedStatusBadge
import android.app.faunadex.presentation.components.RarityBadge
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.presentation.components.RibbonBadge
import android.app.faunadex.presentation.components.FunFactDialog
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.ui.Alignment

@Composable
fun AnimalDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AnimalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userEducationLevel = viewModel.currentUserEducationLevel

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = "Animal Detail",
                onNavigateBack = onNavigateBack,
                showBadge = true,
                level = getEducationLevelWithColor(userEducationLevel)
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        when (uiState) {
            is AnimalDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = PastelYellow,
                        strokeWidth = 6.dp
                    )
                }
            }
            is AnimalDetailUiState.Success -> {
                AnimalDetailContent(
                    animal = (uiState as AnimalDetailUiState.Success).animal,
                    userEducationLevel = userEducationLevel,
                    onAudioClick = {},
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is AnimalDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as AnimalDetailUiState.Error).message,
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalDetailContent(
    animal: Animal,
    userEducationLevel: String,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AnimalDetailTab.INFO) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = animal.imageUrl ?: R.drawable.animal_dummy,
            contentDescription = animal.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.animal_dummy),
            error = painterResource(R.drawable.animal_dummy)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 240.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = DarkForest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                if (shouldShowContent(userEducationLevel, minLevel = EducationLevelRequirement.SMP)) {
                    TabIndicators(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )

                    Spacer(Modifier.height(24.dp))
                }


                when (selectedTab) {
                    AnimalDetailTab.INFO -> {
                        InfoTabContent(
                            animal = animal,
                            userEducationLevel = userEducationLevel,
                            onAudioClick = onAudioClick
                        )
                    }
                    AnimalDetailTab.POPULATION -> {
                        PopulationTabContent(animal = animal)
                    }
                    AnimalDetailTab.HABITAT -> {
                        HabitatTabContent(animal = animal)
                    }
                }
            }
        }
    }
}

enum class AnimalDetailTab(val title: String) {
    INFO("Info"),
    POPULATION("Population"),
    HABITAT("Habitat")
}

@Composable
fun TabIndicators(
    selectedTab: AnimalDetailTab,
    onTabSelected: (AnimalDetailTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimalDetailTab.entries.forEach { tab ->
            TabItem(
                text = tab.title,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontFamily = JerseyFont,
            fontSize = 22.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PastelYellow else MediumGreenSage
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(if (isSelected) 60.dp else 0.dp)
                .height(3.dp)
                .background(
                    color = if (isSelected) PastelYellow else androidx.compose.ui.graphics.Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoTabContent(
    animal: Animal,
    userEducationLevel: String,
    onAudioClick: () -> Unit
) {
    var showFunFactDialog by remember { mutableStateOf(false) }

    Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = animal.name,
                            fontFamily = PoppinsFont,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PastelYellow
                        )

                        if (shouldShowContent(userEducationLevel, minLevel = EducationLevelRequirement.SMP)) {
                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = animal.scientificName,
                                fontFamily = PoppinsFont,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                                color = MediumGreenSage
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Always show Conservation Status
                            ConservationStatusBadge(
                                status = animal.conservationStatus,
                                showFullName = true
                            )

                            when (userEducationLevel) {
                                "SD" -> {
                                    if (animal.rarityLevel.isNotEmpty()) {
                                        RarityBadge(rarityLevel = animal.rarityLevel)
                                    }
                                    if (animal.endemicStatus.isNotEmpty()) {
                                        EndemicStatusBadge(status = animal.endemicStatus)
                                    }
                                }
                                "SMP" -> {
                                    if (animal.endemicStatus.isNotEmpty()) {
                                        EndemicStatusBadge(status = animal.endemicStatus)
                                    }
                                    if (animal.populationTrend.isNotEmpty()) {
                                        PopulationTrendBadge(trend = animal.populationTrend)
                                    }
                                    if (animal.isProtected) {
                                        ProtectedStatusBadge(
                                            isProtected = animal.isProtected,
                                            protectionType = animal.protectionType
                                        )
                                    }
                                }
                                "SMA" -> {
                                    if (animal.endemicStatus.isNotEmpty()) {
                                        EndemicStatusBadge(status = animal.endemicStatus)
                                    }
                                    if (animal.populationTrend.isNotEmpty()) {
                                        PopulationTrendBadge(trend = animal.populationTrend)
                                    }
                                    if (animal.activityPeriod.isNotEmpty()) {
                                        ActivityPeriodBadge(period = animal.activityPeriod)
                                    }
                                    if (animal.isProtected) {
                                        ProtectedStatusBadge(
                                            isProtected = animal.isProtected,
                                            protectionType = animal.protectionType
                                        )
                                    }
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onAudioClick,
                        icon = Icons.AutoMirrored.Outlined.VolumeUp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = animal.description,
                    fontSize = 18.sp,
                    color = MediumGreenSage,
                    fontFamily = JerseyFont,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    when (userEducationLevel) {
                        "SD" -> {
                            RibbonBadge(
                                icon = if (animal.diet.contains("Carnivore", ignoreCase = true)) {
                                    Icons.Outlined.Pets
                                } else {
                                    Icons.Outlined.Eco
                                },
                                text = animal.diet.ifEmpty { "Omnivore" },
                                hexagonBackgroundColor = PrimaryGreenLight
                            )

                            RibbonBadge(
                                icon = Icons.Outlined.Star,
                                text = animal.specialTitle.ifEmpty { "Unique" },
                                hexagonBackgroundColor = PrimaryGreenLight
                            )
                        }
                        "SMP", "SMA" -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = { showFunFactDialog = true },
                                    icon = Icons.AutoMirrored.Outlined.HelpCenter,
                                    size = 50.dp,
                                    cornerRadius = 8.dp
                                )

                                Text(
                                    text = "Fun Fact",
                                    fontFamily = JerseyFont,
                                    color = MediumGreenSage,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }

    // Fun Fact Dialog
    FunFactDialog(
        title = "Fun Fact",
        content = animal.funFact.ifEmpty { "No fun fact available for this animal yet." },
        onDismiss = { showFunFactDialog = false },
        showDialog = showFunFactDialog
    )
}

@Composable
fun PopulationTabContent(animal: Animal) {
    Column {
        Text(
            text = "Population Information",
            fontFamily = PoppinsFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Population data and statistics for ${animal.name} will be displayed here.",
            fontSize = 16.sp,
            color = MediumGreenSage,
            fontFamily = PoppinsFont
        )
    }
}

@Composable
fun HabitatTabContent(animal: Animal) {
    Column {
        Text(
            text = "Habitat Information",
            fontFamily = PoppinsFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = animal.habitat,
            fontSize = 16.sp,
            color = MediumGreenSage,
            fontFamily = PoppinsFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSDPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SD", ErrorRed)
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            AnimalDetailContent(
                modifier = Modifier.padding(paddingValues),
                animal = Animal(
                    id = "1",
                    name = "Komodo Dragon",
                    scientificName = "Varanus komodoensis",
                    category = "Reptile",
                    habitat = "Grasslands and forests",
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    rarityLevel = "Rare"
                ),
                onAudioClick = {},
                userEducationLevel = "SD"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSMPPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SMP", PrimaryBlue)
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            AnimalDetailContent(
                modifier = Modifier.padding(paddingValues),
                animal = Animal(
                    id = "1",
                    name = "Komodo Dragon",
                    scientificName = "Varanus komodoensis",
                    category = "Reptile",
                    habitat = "Grasslands and forests",
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    populationTrend = "Decreasing",
                    isProtected = true,
                    protectionType = "CITES Listed",
                    funFact = "Komodo dragons can eat up to 80% of their body weight in one meal! They are also excellent swimmers and can dive up to 4.5 meters deep."
                ),
                onAudioClick = {},
                userEducationLevel = "SMP"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSMAPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SMA", BlueOcean)
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            AnimalDetailContent(
                modifier = Modifier.padding(paddingValues),
                animal = Animal(
                    id = "1",
                    name = "Komodo Dragon",
                    scientificName = "Varanus komodoensis",
                    category = "Reptile",
                    habitat = "Grasslands and forests",
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    populationTrend = "Decreasing",
                    activityPeriod = "Diurnal",
                    isProtected = true,
                    protectionType = "National Park Species",
                    funFact = "Komodo dragons can eat up to 80% of their body weight in one meal! They are also excellent swimmers and can dive up to 4.5 meters deep. Female Komodo dragons can reproduce through parthenogenesis, meaning they can lay fertile eggs without mating with a male."
                ),
                onAudioClick = {},
                userEducationLevel = "SMA"
            )
        }
    }
}

private fun getEducationLevelWithColor(level: String): EducationLevel {
    return when (level) {
        "SD" -> EducationLevel("SD", ErrorRed)
        "SMP" -> EducationLevel("SMP", PrimaryBlue)
        "SMA" -> EducationLevel("SMA", BlueOcean)
        else -> EducationLevel("SMA", BlueOcean) // Default to SMA
    }
}

/**
 * Education level requirement enum for content visibility
 * SD = 1, SMP = 2, SMA = 3
 */
enum class EducationLevelRequirement(val level: Int) {
    SD(1),
    SMP(2),
    SMA(3)
}

/**
 * Helper function to determine if content should be shown based on user's education level
 * @param userLevel Current user's education level (SD, SMP, or SMA)
 * @param minLevel Minimum education level required to view the content
 * @return true if user's level meets or exceeds the minimum requirement
 */
private fun shouldShowContent(
    userLevel: String,
    minLevel: EducationLevelRequirement
): Boolean {
    val userLevelValue = when (userLevel) {
        "SD" -> EducationLevelRequirement.SD.level
        "SMP" -> EducationLevelRequirement.SMP.level
        "SMA" -> EducationLevelRequirement.SMA.level
        else -> EducationLevelRequirement.SMA.level
    }
    return userLevelValue >= minLevel.level
}
