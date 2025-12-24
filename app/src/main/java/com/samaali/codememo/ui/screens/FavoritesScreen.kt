// ui/screens/FavoritesScreen.kt
package com.samaali.codememo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.remote.creation.first
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.model.UserExercise
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.data.repository.UserExerciseRepository
import com.samaali.codememo.ui.navigation.Screen
import com.samaali.codememo.ui.utils.FavoriteManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val context = LocalContext.current
    val algoRepo = remember { AlgorithmRepository(context) }
    val userRepo = remember { UserExerciseRepository(context) }
    val scope = rememberCoroutineScope()

    // États pour les listes favoris
    var favoriteAlgos by remember { mutableStateOf<List<Algorithm>>(emptyList()) }
    var favoriteUserExos by remember { mutableStateOf<List<UserExercise>>(emptyList()) }

    // Chargement des données
    LaunchedEffect(Unit) {
        scope.launch {
            // Récupère les IDs favoris
            val algoIds = FavoriteManager.getFavoriteAlgoList(context)
            val userExoIds = FavoriteManager.getFavoriteUserExoList(context)

            // Charge les algorithmes standards favoris
            val allAlgos = algoRepo.getAllAlgorithms()
            favoriteAlgos = allAlgos.filter { algoIds.contains(it.id) }

            // Charge les exercices personnalisés favoris
            val allUserExos = userRepo.getAll().first()
            favoriteUserExos = allUserExos.filter { userExoIds.contains(it.id) }
        }
    }

    val totalFavorites = favoriteAlgos.size + favoriteUserExos.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mes Favoris ($totalFavorites)",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        if (totalFavorites == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucun favori pour l’instant.\nAjoutez-en avec le ❤️ !",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // === Section : Algorithmes standards ===
                if (favoriteAlgos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Algorithmes standards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(favoriteAlgos) { algo ->
                        Card(
                            onClick = {
                                navController.navigate(Screen.AlgoDetail.createRoute(algo.id))
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text(algo.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                supportingContent = {
                                    Text(
                                        text = algo.description.take(100) + if (algo.description.length > 100) "..." else "",
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            )
                        }
                    }
                }

                // === Section : Mes exercices personnalisés ===
                if (favoriteUserExos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Mes exercices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(favoriteUserExos) { exo ->
                        Card(
                            onClick = {
                                navController.navigate(Screen.UserDetail.createRoute(exo.id))
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text(exo.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                supportingContent = {
                                    if (exo.description.isNotBlank()) {
                                        Text(
                                            text = exo.description.take(100) + if (exo.description.length > 100) "..." else "",
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}