package com.samaali.codememo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samaali.codememo.ui.screen.PythonExecutionScreen
import com.samaali.codememo.ui.screens.AlgorithmDetailScreen
import com.samaali.codememo.ui.screens.FavoritesScreen
import com.samaali.codememo.ui.screens.HomeScreen
import com.samaali.codememo.ui.screens.MyExosScreen
import com.samaali.codememo.ui.screens.ProfileScreen

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
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.MyExos.route) { MyExosScreen(navController) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }

            // Détail algorithme standard
            composable(
                route = Screen.AlgoDetail.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                AlgorithmDetailScreen(
                    algorithmId = id,
                    userExerciseId = null,
                    navController = navController
                )
            }

            // Détail exercice personnalisé
            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                AlgorithmDetailScreen(
                    algorithmId = null,
                    userExerciseId = id,
                    navController = navController
                )
            }

            // Exécution algorithme standard
            composable(
                route = Screen.AlgoExecute.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                PythonExecutionScreen(
                    algorithmId = id,
                    userExerciseId = null,
                    navController = navController
                )
            }

            // Exécution exercice personnalisé
            composable(
                route = Screen.UserExecute.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                PythonExecutionScreen(
                    algorithmId = null,
                    userExerciseId = id,
                    navController = navController
                )
            }
        }
    }
}