// data/repository/AlgorithmRepository.kt
package com.samaali.codememo.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samaali.codememo.data.model.Algorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlgorithmRepository(private val context: Context) {

    private var cache: List<Algorithm>? = null

    suspend fun getAllAlgorithms(): List<Algorithm> = withContext(Dispatchers.IO) {
        cache?.let { return@withContext it }

        val json = context.assets.open("algorithms.json")
            .bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<Algorithm>>() {}.type
        val list = Gson().fromJson<List<Algorithm>>(json, type)
        cache = list
        list
    }

    suspend fun getAlgorithmById(id: Int): Algorithm? =
        getAllAlgorithms().find { it.id == id }
}