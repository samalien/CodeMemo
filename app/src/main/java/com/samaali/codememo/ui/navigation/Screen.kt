package com.samaali.codememo.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object MyExos : Screen("my_exos")
    data object Favorites : Screen("favorites")
    data object Profile : Screen("profile")

    // Détail algorithme standard
    data object AlgoDetail : Screen("algo_detail/{algorithmId}") {
        fun createRoute(id: Int) = "algo_detail/$id"
    }

    // Détail exercice personnalisé
    data object UserDetail : Screen("user_detail/{exerciseId}") {
        fun createRoute(id: Int) = "user_detail/$id"
    }

    // Exécution algorithme standard
    data object AlgoExecute : Screen("algo_execute/{algorithmId}") {
        fun createRoute(id: Int) = "algo_execute/$id"
    }

    // Exécution exercice personnalisé
    data object UserExecute : Screen("user_execute/{exerciseId}") {
        fun createRoute(id: Int) = "user_execute/$id"
    }
}