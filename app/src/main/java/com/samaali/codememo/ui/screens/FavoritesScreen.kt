// ui/screens/FavoritesScreen.kt
package com.samaali.codememo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.ui.utils.FavoriteManager
import androidx.compose.runtime.produceState

@Composable
fun FavoritesScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }
    val favoriteIds = FavoriteManager.getFavoriteList(context)

    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Mes Favoris (${favoriteIds.size})",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(24.dp)
        )

        if (favoriteIds.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    "Aucun favori pour l’instant",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteIds.size) { index ->
                    val id = favoriteIds[index]
                    val algorithm by produceState<Algorithm?>(initialValue = null, key1 = id) {
                        value = repository.getAlgorithmById(id)
                    }

                    algorithm?.let { algo ->
                        Card(
                            onClick = { navController.navigate("detail/$id") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            ListItem(
                                headlineContent = { Text(algo.name) },
                                supportingContent = { Text(algo.category ?: "Algorithme") },
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