// ui/utils/FavoriteSync.kt
package com.samaali.codememo.ui.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FavoriteSync {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION = "users"

    fun getUserFavoritesCollection(userId: String) = db.collection(COLLECTION).document(userId).collection("favorites")

    suspend fun syncFavoritesToCloud(userId: String, favoriteIds: Set<Int>) {
        val data = mapOf("list" to favoriteIds.toList())
        getUserFavoritesCollection(userId).document("data").set(data, SetOptions.merge()).await()
    }

    suspend fun getFavoritesFromCloud(userId: String): List<Int> {
        return try {
            val snapshot = getUserFavoritesCollection(userId).document("data").get().await()
            snapshot.get("list") as? List<Int> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}