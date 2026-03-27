package com.samaali.codememo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.text.Normalizer
import androidx.compose.foundation.lazy.items

fun normalizeText(text: String): String {
    return Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()
}

@Composable
fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }

    val allAlgorithms by produceState<List<Algorithm>?>(initialValue = null) {
        value = repository.getAllAlgorithms()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // 🔥 Catégories spéciales (avancées)
    val specialCategories = listOf(
        "Matrices",
        "Programmation Dynamique",
        "Fichiers",
        "Algorithmes Récursifs",
        "Algorithmes d'Optimisation",
        "Algorithmes d'approximation"
    )

    val categories = remember(allAlgorithms) {
        val cats = allAlgorithms?.map { it.category }?.distinct() ?: emptyList()

        val normal = cats.filter { it !in specialCategories }.sorted()
        val special = cats.filter { it in specialCategories }.sorted()

        listOf("Toutes les catégories") + normal + special
    }

    val categoryCounts = remember(allAlgorithms) {
        allAlgorithms?.groupBy { it.category }?.mapValues { it.value.size } ?: emptyMap()
    }

    val filteredAlgorithms = remember(allAlgorithms, searchQuery, selectedCategory) {
        val normalizedQuery = normalizeText(searchQuery)

        allAlgorithms?.filter { algo ->
            val matchCategory = when (selectedCategory) {
                null, "Toutes les catégories" -> true
                else -> algo.category == selectedCategory
            }

            val normalizedName = normalizeText(algo.name)
            val normalizedCategory = normalizeText(algo.category)

            val matchSearch = searchQuery.isBlank() ||
                    normalizedName.contains(normalizedQuery) ||
                    normalizedCategory.contains(normalizedQuery)

            matchCategory && matchSearch
        } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // HEADER
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

        // SEARCH
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (selectedCategory == null && it.isNotBlank()) {
                    selectedCategory = "Toutes les catégories"
                }
            },
            label = { Text("Rechercher un algorithme...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // VUE CATEGORIES
        // =========================
        if (selectedCategory == null && searchQuery.isBlank()) {

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                itemsIndexed(categories) { index, category ->

                    val isSpecial = category in specialCategories
                    val count = if (category == "Toutes les catégories") {
                        allAlgorithms?.size ?: 0
                    } else {
                        categoryCounts[category] ?: 0
                    }

                    // Divider pour séparer les catégories normales des avancées
                    if (index > 0 && isSpecial && categories[index - 1] !in specialCategories) {
                        Text(
                            text = "✦ Catégories Avancées",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                        )
                    }

                    Card(
                        onClick = {
                            selectedCategory = if (category == "Toutes les catégories")
                                "Toutes les catégories"
                            else category
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSpecial)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSpecial)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
                        else
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSpecial) 8.dp else 3.dp,
                            pressedElevation = if (isSpecial) 12.dp else 6.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Icône spéciale pour les catégories avancées
                                if (isSpecial) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }

                                Column {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (isSpecial) FontWeight.Bold else FontWeight.SemiBold
                                    )

                                    Text(
                                        text = "$count Algorithme${if (count > 1) "s" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSpecial)
                                            MaterialTheme.colorScheme.tertiary
                                        else
                                            MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = if (isSpecial)
                                    MaterialTheme.colorScheme.tertiary
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // =========================
        // VUE ALGORITHMES
        // =========================
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                items(filteredAlgorithms) { algo ->
                    AlgorithmCard(
                        algorithm = algo,
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
                                    text = if (searchQuery.isNotBlank())
                                        "Essayez un autre mot-clé"
                                    else
                                        "Cette catégorie est vide",
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
fun AlgorithmCard(
    algorithm: Algorithm,
    onClick: () -> Unit
) {
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