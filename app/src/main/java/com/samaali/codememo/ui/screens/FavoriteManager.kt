package com.samaali.codememo.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {
    private const val PREF_NAME = "CodeMemoFavorites"
    private const val KEY_ALGO_FAVORITES = "algo_favorites"      // Pour les algorithmes standards
    private const val KEY_USER_FAVORITES = "user_exo_favorites"  // Pour les exercices personnalisés

    private val gson = Gson()

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // === ALGORITHMES STANDARDS ===
    fun addFavoriteAlgo(context: Context, algorithmId: Int) {
        val favorites = getFavoriteAlgos(context).toMutableSet()
        favorites.add(algorithmId)
        saveFavoriteAlgos(context, favorites)
    }

    fun removeFavoriteAlgo(context: Context, algorithmId: Int) {
        val favorites = getFavoriteAlgos(context).toMutableSet()
        favorites.remove(algorithmId)
        saveFavoriteAlgos(context, favorites)
    }

    fun isFavoriteAlgo(context: Context, algorithmId: Int): Boolean {
        return getFavoriteAlgos(context).contains(algorithmId)
    }

    // === EXERCICES PERSONNALISÉS ===
    fun addFavoriteUserExo(context: Context, exerciseId: Int) {
        val favorites = getFavoriteUserExos(context).toMutableSet()
        favorites.add(exerciseId)
        saveFavoriteUserExos(context, favorites)
    }

    fun removeFavoriteUserExo(context: Context, exerciseId: Int) {
        val favorites = getFavoriteUserExos(context).toMutableSet()
        favorites.remove(exerciseId)
        saveFavoriteUserExos(context, favorites)
    }

    fun isFavoriteUserExo(context: Context, exerciseId: Int): Boolean {
        return getFavoriteUserExos(context).contains(exerciseId)
    }

    // === FONCTIONS GÉNÉRIQUES (à utiliser dans AlgorithmDetailScreen) ===
    fun isFavorite(context: Context, itemId: Int, isUserExercise: Boolean): Boolean {
        return if (isUserExercise) {
            isFavoriteUserExo(context, itemId)
        } else {
            isFavoriteAlgo(context, itemId)
        }
    }

    fun toggleFavorite(context: Context, itemId: Int, isUserExercise: Boolean) {
        if (isUserExercise) {
            if (isFavoriteUserExo(context, itemId)) {
                removeFavoriteUserExo(context, itemId)
            } else {
                addFavoriteUserExo(context, itemId)
            }
        } else {
            if (isFavoriteAlgo(context, itemId)) {
                removeFavoriteAlgo(context, itemId)
            } else {
                addFavoriteAlgo(context, itemId)
            }
        }
    }

    // Récupérer toutes les listes (utile pour FavoritesScreen)
    fun getFavoriteAlgoList(context: Context): List<Int> {
        return getFavoriteAlgos(context).toList().sorted()
    }

    fun getFavoriteUserExoList(context: Context): List<Int> {
        return getFavoriteUserExos(context).toList().sorted()
    }

    // Fonctions privées
    private fun getFavoriteAlgos(context: Context): Set<Int> {
        val json = getPrefs(context).getString(KEY_ALGO_FAVORITES, null) ?: return emptySet()
        return try {
            gson.fromJson(json, object : TypeToken<Set<Int>>() {}.type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun saveFavoriteAlgos(context: Context, favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        getPrefs(context).edit().putString(KEY_ALGO_FAVORITES, json).apply()
    }

    private fun getFavoriteUserExos(context: Context): Set<Int> {
        val json = getPrefs(context).getString(KEY_USER_FAVORITES, null) ?: return emptySet()
        return try {
            gson.fromJson(json, object : TypeToken<Set<Int>>() {}.type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun saveFavoriteUserExos(context: Context, favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        getPrefs(context).edit().putString(KEY_USER_FAVORITES, json).apply()
    }

    // Anciennes fonctions (rétrocompatibilité si utilisées ailleurs)
    fun addFavorite(context: Context, algorithmId: Int) = addFavoriteAlgo(context, algorithmId)
    fun removeFavorite(context: Context, algorithmId: Int) = removeFavoriteAlgo(context, algorithmId)
    fun toggleFavorite(context: Context, algorithmId: Int) = toggleFavorite(context, algorithmId, false)
    fun isFavorite(context: Context, algorithmId: Int) = isFavoriteAlgo(context, algorithmId)
    fun getFavoriteList(context: Context) = getFavoriteAlgoList(context)
}