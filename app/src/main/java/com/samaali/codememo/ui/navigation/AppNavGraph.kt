package com.samaali.codememo.ui.navigation
import com.samaali.codememo.ui.screens.*
import androidx.compose.runtime.Composable
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
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable(
            "detail/{algorithmId}",
            arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
            AlgorithmDetailScreen(id, navController)
        }
        composable(
            "execute/{algorithmId}",
            arguments = listOf(navArgument("algorithmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("algorithmId") ?: 0
            PythonExecutionScreen(id)
        }
    }
}
