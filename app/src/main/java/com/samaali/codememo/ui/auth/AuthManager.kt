package com.samaali.codememo.ui.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.BuildConfig
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthManager {

    private const val TAG = "AuthManager"

    private var googleSignInClient: GoogleSignInClient? = null
    private val auth: FirebaseAuth = Firebase.auth

    // ←←← METS TON WEB CLIENT ID ICI (le plus simple) ←←←
    private const val WEB_CLIENT_ID = "100581009842-pp47eipvo9d3mnjtsmdgoa4uiu48s126.apps.googleusercontent.com"


    fun init(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)           // Utilisation directe
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Activation de l'émulateur en mode Debug
        if (BuildConfig.DEBUG) {
            try {
                auth.useEmulator("10.0.2.2", 9099)
                Log.d(TAG, "✅ Firebase Auth Emulator activé sur 10.0.2.2:9099")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Impossible d'activer l'émulateur Auth", e)
            }
        }
    }

    fun getSignInIntent(): Intent? = googleSignInClient?.signInIntent

    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): Boolean {
        return try {
            val account = task.getResult(ApiException::class.java)
                ?: throw Exception("GoogleSignInAccount est null")

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()

            Log.d(TAG, "✅ Connexion Google réussie - UID: ${result.user?.uid}")
            true

        } catch (e: ApiException) {
            Log.e(TAG, "❌ Google Sign-In échoué - Code: ${e.statusCode}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erreur lors de la connexion", e)
            false
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun signOut() {
        auth.signOut()
        googleSignInClient?.signOut()
        Log.d(TAG, "Utilisateur déconnecté")
    }

    fun revokeAccess() {
        auth.signOut()
        googleSignInClient?.revokeAccess()
    }
}