package com.samaali.codememo.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.samaali.codememo.ui.auth.AuthManager
import com.samaali.codememo.ui.navigation.StatCard
import com.samaali.codememo.ui.utils.FavoriteManager
import com.samaali.codememo.ui.utils.FavoriteSync
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = AuthManager.getCurrentUser()
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            isLoading = true
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            scope.launch {
                AuthManager.handleSignInResult(task)
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        AuthManager.init(context)
    }

    LaunchedEffect(user) {
        if (user != null) {
            // Sync favoris ici si tu veux
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        if (user != null) {
            AsyncImage(
                model = user.photoUrl ?: "https://ui-avatars.com/api/?name=${user.displayName}&background=4CAF50&color=fff&size=256",
                contentDescription = "Photo",
                modifier = Modifier.size(140.dp).clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))

            Text(user.displayName ?: "Utilisateur", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(user.email ?: "", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(48.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("127", "Algos")
                StatCard("42", "Favoris")
                StatCard("15", "Exécutés")
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    isLoading = true
                    AuthManager.signOut(context)
                    isLoading = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(Modifier.width(8.dp))
                Text("Se déconnecter")
            }
        } else {
            Box(
                modifier = Modifier.size(140.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("?", fontSize = 60.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Spacer(Modifier.height(24.dp))

            Text("Non connecté", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = {
                    val intent = AuthManager.getSignInIntent()
                    if (intent != null) {
                        launcher.launch(intent)
                    }
                }
            ) {
                Icon(Icons.Default.Login, null)
                Spacer(Modifier.width(8.dp))
                Text("Connexion avec Google")
            }
        }
    }
}