package com.samaali.codememo.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.samaali.codememo.R

@Composable
fun CodeMemoBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isInAlgoContext = currentRoute?.startsWith("algo_detail/") == true ||
            currentRoute?.startsWith("algo_execute/") == true

    val isInUserContext = currentRoute?.startsWith("user_detail/") == true ||
            currentRoute?.startsWith("user_execute/") == true

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.MyExos.route,
        Screen.Favorites.route,
        Screen.Profile.route
    ) || isInAlgoContext || isInUserContext

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 12.dp
        ) {
            NavigationBarItem(
                selected = currentRoute == Screen.Home.route || isInAlgoContext,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_home), "Accueil") },
                label = { Text("Accueil") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Screen.MyExos.route || isInUserContext,
                onClick = {
                    navController.navigate(Screen.MyExos.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_book), "Mes Exos") },
                label = { Text("Mes Exos") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Screen.Favorites.route,
                onClick = {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_favorite), "Favoris") },
                label = { Text("Favoris") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Screen.Profile.route,
                onClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_person), "Profil") },
                label = { Text("Profil") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                )
            )
        }
    }
}