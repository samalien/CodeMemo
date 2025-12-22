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
import com.samaali.codememo.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { CodeMemoBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            // Le padding ici réserve l'espace pour la BottomBar
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.MyExos.route) { MyExosScreen(navController) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                AlgorithmDetailScreen(algorithmId = id, userExerciseId = id, navController = navController)
            }

            composable(
                route = Screen.Execute.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                PythonExecutionScreen(algorithmId = id, userExerciseId = id, navController = navController)
            }
        }
    }
}