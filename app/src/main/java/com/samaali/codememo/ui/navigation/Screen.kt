package com.samaali.codememo.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object MyExos : Screen("my_exos")        // ← Ton nouvel écran
    data object Favorites : Screen("favorites")
    data object Profile : Screen("profile")       // ← Profil correct
    data object Detail : Screen("detail/{algorithmId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
    data object Execute : Screen("execute/{algorithmId}") {
        fun createRoute(id: Int) = "execute/$id"
    }
    data object UserExecute : Screen("user_execute/{exerciseId}") {
        fun createRoute(id: Int) = "user_execute/$id"
    }

}