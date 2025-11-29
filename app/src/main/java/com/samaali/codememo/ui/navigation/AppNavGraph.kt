package com.samaali.codememo.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samaali.codememo.ui.screen.PythonExecutionScreen
import com.samaali.codememo.ui.screens.AlgorithmDetailScreen
import com.samaali.codememo.ui.screens.HomeScreen
import androidx.navigation.NavType
import androidx.compose.runtime.*
import com.samaali.codememo.ui.screens.FavoritesScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { CodeMemoBottomBar(navController) }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }

            // ÉCRAN RECHERCHE – Ultra moderne
            composable(Screen.Search.route) {
                SearchScreen()
            }

            // ÉCRAN FAVORIS – Grille premium
            composable(Screen.Favorites.route) { FavoritesScreen(navController) }

            // ÉCRAN PROFIL – Style 2025
            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            // Tes écrans existants
            composable(
                Screen.Detail.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                AlgorithmDetailScreen(id, navController)
            }

            composable(
                Screen.Execute.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                PythonExecutionScreen(id)
            }
        }
    }
}

// ÉCRAN RECHERCHE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Rechercher un algorithme…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, null) } },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Résultats de recherche (optionnel plus tard)
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(15) { index ->
                Card(
                    onClick = { },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    ListItem(
                        headlineContent = { Text("Algorithme ${index + 1} • Tri rapide") },
                        supportingContent = { Text("Python • O(n log n)") },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                    )
                }
            }
        }
    }
}

// ÉCRAN FAVORIS

// ÉCRAN PROFIL
@Composable
fun ProfileScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // Photo de profil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "SA",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Sama Ali", style = MaterialTheme.typography.headlineSmall)
        Text("Développeur passionné • Android", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(40.dp))

        // Stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatCard("127", "Algos")
            StatCard("42", "Favoris")
            StatCard("15", "Exécutés")
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Modifier le profil")
        }
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}