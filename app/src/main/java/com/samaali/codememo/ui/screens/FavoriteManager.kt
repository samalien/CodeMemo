// ui/utils/FavoriteManager.kt
package com.samaali.codememo.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {
    private const val PREF_NAME = "CodeMemoFavorites"
    private const val KEY_FAVORITES = "favorites"
    private val gson = Gson()

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun addFavorite(context: Context, algorithmId: Int) {
        val favorites = getFavorites(context).toMutableSet()
        favorites.add(algorithmId)
        saveFavorites(context, favorites)
    }

    fun removeFavorite(context: Context, algorithmId: Int) {
        val favorites = getFavorites(context).toMutableSet()
        favorites.remove(algorithmId)
        saveFavorites(context, favorites)
    }

    fun toggleFavorite(context: Context, algorithmId: Int) {
        if (isFavorite(context, algorithmId)) {
            removeFavorite(context, algorithmId)
        } else {
            addFavorite(context, algorithmId)
        }
    }

    fun isFavorite(context: Context, algorithmId: Int): Boolean {
        return getFavorites(context).contains(algorithmId)
    }

    fun getFavoriteList(context: Context): List<Int> {
        return getFavorites(context).toList().sorted()
    }

    private fun getFavorites(context: Context): Set<Int> {
        val json = getPrefs(context).getString(KEY_FAVORITES, null) ?: return emptySet()
        return try {
            gson.fromJson(json, object : TypeToken<Set<Int>>() {}.type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun saveFavorites(context: Context, favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply()
    }
}