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
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }

            composable(Screen.MyExos.route) {
                MyExosScreen(navController)
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(navController)
            }

            // PROFIL : vrai écran avec connexion Google
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }

            // DÉTAIL : même route pour algos standards ET exos perso
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                AlgorithmDetailScreen(
                    algorithmId = id,
                    userExerciseId = id,
                    navController = navController
                )
            }

            // EXÉCUTION : même route pour les deux types
            composable(
                route = Screen.Execute.route,
                arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
                // On passe l'ID (algo ou exo perso) comme algorithmId
                // Ton PythonExecutionScreen chargera le bon code grâce à la logique interne
                PythonExecutionScreen(algorithmId = id, navController = navController)
            }
        }
    }
}