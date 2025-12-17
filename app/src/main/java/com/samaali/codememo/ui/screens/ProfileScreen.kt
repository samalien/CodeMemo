package com.samaali.codememo.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.samaali.codememo.ui.auth.AuthManager
import com.samaali.codememo.ui.utils.FavoriteManager
import com.samaali.codememo.ui.utils.FavoriteSync
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import android.content.ContextWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = AuthManager.getCurrentUser()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            scope.launch {
                val success = AuthManager.handleSignInResult(task)
                if (success) {
                    // Sync favoris locaux → cloud
                    val localFavorites = FavoriteManager.getFavoriteList(context).toSet()
                    AuthManager.getCurrentUser()?.uid?.let { uid ->
                        FavoriteSync.syncFavoritesToCloud(uid, localFavorites)
                    }
                }
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null) {
            val cloudFavorites = FavoriteSync.getFavoritesFromCloud(user.uid)
            FavoriteManager.saveAll(context, cloudFavorites.toSet())
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        if (user != null) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Photo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(16.dp))
            Text(user.displayName ?: "Utilisateur", style = MaterialTheme.typography.headlineSmall)
            Text(user.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("?", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.height(16.dp))
            Text("Non connecté", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(Modifier.height(40.dp))

        if (user != null) {
            Button(
                onClick = { AuthManager.signOut(context) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Se déconnecter")
            }
        } else {
            Button(onClick = {
                AuthManager.init(context)
                launcher.launch(AuthManager.getSignInIntent())
            }) {
                Text("Connexion avec Google")
            }
        }
    }
}