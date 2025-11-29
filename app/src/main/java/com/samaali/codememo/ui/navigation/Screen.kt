package com.samaali.codememo.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Profile : Screen("profile")
    data object Detail : Screen("detail/{algorithmId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
    data object Execute : Screen("execute/{algorithmId}") {
        fun createRoute(id: Int) = "execute/$id"
    }
}