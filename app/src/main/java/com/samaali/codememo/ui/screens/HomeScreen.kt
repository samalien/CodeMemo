package com.samaali.codememo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }

    // Chargement unique des algorithmes
    val allAlgorithms by produceState<List<Algorithm>?>(initialValue = null) {
        value = repository.getAllAlgorithms()
    }

    // États locaux
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) } // null = vue catégories

    // Catégories uniques + compteur
    val categories = remember(allAlgorithms) {
        val cats = allAlgorithms?.map { it.category }?.distinct() ?: emptyList()
        listOf("Toutes les catégories") + cats.sorted()
    }

    val categoryCounts = remember(allAlgorithms) {
        allAlgorithms?.groupBy { it.category }?.mapValues { it.value.size } ?: emptyMap()
    }

    // Filtrage combiné : texte + catégorie
    val filteredAlgorithms = remember(allAlgorithms, searchQuery, selectedCategory) {
        allAlgorithms?.filter { algo ->
            // Filtre catégorie
            val matchCategory = when (selectedCategory) {
                null, "Toutes les catégories" -> true
                else -> algo.category == selectedCategory
            }
            // Filtre recherche texte
            val matchSearch = searchQuery.isBlank() ||
                    algo.name.contains(searchQuery, ignoreCase = true) ||
                    algo.category.contains(searchQuery, ignoreCase = true)

            matchCategory && matchSearch
        } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // === En-tête : titre + bouton retour si dans une catégorie ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedCategory != null && selectedCategory != "Toutes les catégories") {
                IconButton(onClick = { selectedCategory = null }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                }
            }
            Text(
                text = when {
                    selectedCategory == null -> "CodeMemo - Catégories"
                    selectedCategory == "Toutes les catégories" -> "Tous les algorithmes"
                    else -> selectedCategory!!
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === Barre de recherche toujours visible ===
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                // Si on commence à taper, on passe automatiquement à la vue "algorithmes"
                if (selectedCategory == null && newQuery.isNotBlank()) {
                    selectedCategory = "Toutes les catégories"
                }
            },
            label = { Text("Rechercher un algorithme...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Rechercher") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === Vue 1 : Liste des catégories (quand rien sélectionné et pas de recherche) ===
        if (selectedCategory == null && searchQuery.isBlank()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    val count = if (category == "Toutes les catégories") {
                        allAlgorithms?.size ?: 0
                    } else {
                        categoryCounts[category] ?: 0
                    }
                    Card(
                        onClick = {
                            selectedCategory = if (category == "Toutes les catégories") {
                                "Toutes les catégories"
                            } else {
                                category
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$count algorithme${if (count > 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
        // === Vue 2 : Liste des algorithmes filtrés ===
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredAlgorithms) { algo ->
                    AlgorithmCard(
                        algorithm = algo,
                        // ← CHANGEMENT IMPORTANT ICI : nouvelle route
                        onClick = {
                            navController.navigate(Screen.AlgoDetail.createRoute(algo.id))
                        }
                    )
                }

                if (filteredAlgorithms.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "Aucun résultat" else "Aucun algorithme",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (searchQuery.isNotBlank()) "Essayez un autre mot-clé" else "Cette catégorie est vide",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmCard(algorithm: Algorithm, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = algorithm.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = algorithm.category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}