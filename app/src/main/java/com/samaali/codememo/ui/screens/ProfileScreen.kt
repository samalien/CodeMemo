package com.samaali.codememo.ui.screens

import android.app.Activity
import android.util.Log
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
        Log.d("AUTH_DEBUG", "Result code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            isLoading = true
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            scope.launch {
                AuthManager.handleSignInResult(task)
                //Log.d("AUTH_DEBUG", "Success: $success")
                isLoading = false
            }
        }     else {
        Log.d("AUTH_DEBUG", "Connexion annulée ou échouée")
    }
    }

    LaunchedEffect(Unit) {
        AuthManager.init(context)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        if (user != null) {
            // Photo de profil Google
            AsyncImage(
                model = user.photoUrl ?: "https://ui-avatars.com/api/?name=${user.displayName}&background=4CAF50&color=fff&size=256",
                contentDescription = "Photo de profil",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = user.displayName ?: "Utilisateur",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email ?: "",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // === Statistiques (remplace StatCard) ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "127", label = "Algos vus")
                StatItem(value = "42", label = "Favoris")
                StatItem(value = "15", label = "Exécutés")
            }

            Spacer(Modifier.height(40.dp))

            // Bouton Déconnexion
            Button(
                onClick = {
                    isLoading = true
                    AuthManager.signOut()
                    isLoading = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Se déconnecter")
            }
        } else {
            // Utilisateur non connecté
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                Icon(Icons.Default.Login, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Connexion avec Google")
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// Composable simple pour remplacer StatCard
@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}