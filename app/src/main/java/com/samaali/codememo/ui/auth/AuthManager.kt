// ui/auth/AuthManager.kt
package com.samaali.codememo.ui.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object AuthManager {
    private var googleSignInClient: GoogleSignInClient? = null
    private val auth = FirebaseAuth.getInstance()

    fun init(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("267064843577-4t0r0v5u1g5s3b1k9p2m7q8r9s0t1u2.apps.googleusercontent.com") // Ton Web Client ID (correct)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent? {
        return googleSignInClient?.signInIntent
    }

    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): Boolean {
        return try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).await()
            true
        } catch (e: ApiException) {
            // Log l'erreur si tu veux (optionnel)
            false
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut(context: Context) {
        auth.signOut()
        googleSignInClient?.signOut()
    }

    fun revokeAccess(context: Context) {
        auth.signOut()
        googleSignInClient?.revokeAccess()
    }
}