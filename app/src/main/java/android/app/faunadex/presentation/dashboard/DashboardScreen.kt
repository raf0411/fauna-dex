package android.app.faunadex.presentation.dashboard

import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.FaunaCard
import android.app.faunadex.presentation.components.FaunaTopBar
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenShade
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            onNavigateToLogin()
        }
    }

    DashboardScreenContent(
        uiState = uiState,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToProfile: () -> Unit,
    currentRoute: String = "dashboard"
) {
    var searchQuery by remember { mutableStateOf("") }
    var loadedItemsCount by remember { mutableIntStateOf(10) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var lastLoadedCount by remember { mutableIntStateOf(10) }
    val listState = rememberLazyGridState()

    Scaffold(
        topBar = {
            FaunaTopBar(backgroundColor = PrimaryGreen)
        },
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "quiz" -> { /* TODO: Navigate to quiz */ }
                        "dashboard" -> { /* Already on dashboard */ }
                    }
                }
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    label = "Search your Fauna...",
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(start = 6.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DarkGreenShade
                        )
                    }
                )

                IconButton(
                    onClick = { /* TODO: Handle filter click */ }
                )
            }

            Spacer(Modifier.height(32.dp))

            val allFaunaList = remember {
                listOf(
                    Triple("Sumatran Tiger", "Panthera tigris sumatrae", 0),
                    Triple("Komodo Dragon", "Varanus komodoensis", 1),
                    Triple("Javan Rhinoceros", "Rhinoceros sondaicus", 2),
                    Triple("Orangutan", "Pongo pygmaeus", 3),
                    Triple("Bali Starling", "Leucopsar rothschildi", 4),
                    Triple("Proboscis Monkey", "Nasalis larvatus", 5),
                    Triple("Anoa", "Bubalus depressicornis", 6),
                    Triple("Cenderawasih", "Paradisaea apoda", 7),
                    Triple("Maleo Bird", "Macrocephalon maleo", 8),
                    Triple("Tarsius", "Tarsius tarsier", 9),
                    Triple("Banteng", "Bos javanicus", 10),
                    Triple("Sun Bear", "Helarctos malayanus", 11),
                    Triple("Clouded Leopard", "Neofelis nebulosa", 12),
                    Triple("Slow Loris", "Nycticebus coucang", 13),
                    Triple("Malayan Tapir", "Tapirus indicus", 14),
                    Triple("Sunda Pangolin", "Manis javanica", 15),
                    Triple("Javan Hawk-Eagle", "Nisaetus bartelsi", 16),
                    Triple("Black Macaque", "Macaca nigra", 17),
                    Triple("Babirusa", "Babyrousa babyrussa", 18),
                    Triple("Javan Gibbon", "Hylobates moloch", 19),
                    Triple("Asian Elephant", "Elephas maximus", 20),
                    Triple("Green Turtle", "Chelonia mydas", 21),
                    Triple("Whale Shark", "Rhincodon typus", 22),
                    Triple("Manta Ray", "Mobula birostris", 23),
                    Triple("Dugong", "Dugong dugon", 24),
                    Triple("Saltwater Crocodile", "Crocodylus porosus", 25),
                    Triple("False Gharial", "Tomistoma schlegelii", 26),
                    Triple("Rafflesia", "Rafflesia arnoldii", 27),
                    Triple("Javan Warty Pig", "Sus verrucosus", 28),
                    Triple("Sumatran Rhino", "Dicerorhinus sumatrensis", 29)
                )
            }

            val filteredFaunaList = remember(searchQuery) {
                if (searchQuery.isBlank()) {
                    allFaunaList
                } else {
                    allFaunaList.filter { (name, latinName, _) ->
                        name.contains(searchQuery, ignoreCase = true) ||
                                latinName.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            LaunchedEffect(searchQuery) {
                loadedItemsCount = 10
                lastLoadedCount = 10
                isLoadingMore = false
            }

            val displayedFaunaList = remember(filteredFaunaList, loadedItemsCount) {
                filteredFaunaList.take(loadedItemsCount)
            }

            val hasMoreItems = displayedFaunaList.size < filteredFaunaList.size

            LaunchedEffect(listState, filteredFaunaList.size) {
                snapshotFlow {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                    lastVisible?.index
                }
                    .collect { lastVisibleIndex ->
                        if (lastVisibleIndex != null &&
                            lastVisibleIndex >= loadedItemsCount - 3 &&
                            loadedItemsCount < filteredFaunaList.size &&
                            !isLoadingMore &&
                            loadedItemsCount == lastLoadedCount) {
                            isLoadingMore = true
                            kotlinx.coroutines.delay(500)
                            val newCount = minOf(loadedItemsCount + 10, filteredFaunaList.size)
                            loadedItemsCount = newCount
                            lastLoadedCount = newCount
                            isLoadingMore = false
                        }
                    }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayedFaunaList.size) { index ->
                    val (name, latinName, id) = displayedFaunaList[index]


                    FaunaCard(
                        faunaName = name,
                        latinName = latinName,
                        imageUrl = null,
                        isFavorite = id % 3 == 0,
                        onFavoriteClick = { /* TODO: Handle favorite toggle */ },
                        onCardClick = { /* TODO: Navigate to fauna detail */ }
                    )
                }

                if (isLoadingMore && hasMoreItems) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = PrimaryGreen
                            )
                        }
                    }
                }

                if (!hasMoreItems && displayedFaunaList.isNotEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No more fauna to load",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                user = User(
                    uid = "abc123xyz456",
                    email = "test@example.com",
                    username = "TestUser"
                ),
                isSignedOut = false
            ),
            onNavigateToProfile = {}
        )
    }
}

