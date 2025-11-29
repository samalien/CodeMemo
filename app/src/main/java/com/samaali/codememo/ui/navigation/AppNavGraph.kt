package com.samaali.codememo.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samaali.codememo.ui.screen.PythonExecutionScreen
import com.samaali.codememo.ui.screens.AlgorithmDetailScreen
import com.samaali.codememo.ui.screens.HomeScreen
import androidx.navigation.NavType

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { CodeMemoBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Search.route) {
                // Écran temporaire pour tester
                Scaffold { innerPadding ->
                    Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                        Text("ÉCRAN RECHERCHE", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            composable(Screen.Favorites.route) {
                Scaffold { innerPadding ->
                    Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                        Text("ÉCRAN FAVORIS", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            composable(Screen.Profile.route) {
                Scaffold { innerPadding ->
                    Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                        Text("ÉCRAN PROFIL", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Tes écrans existants (garde-les)
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