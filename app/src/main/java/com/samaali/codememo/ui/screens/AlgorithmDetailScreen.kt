package com.samaali.codememo.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.ui.utils.FavoriteManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmDetailScreen(algorithmId: Int, navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }

    val algorithm by produceState<Algorithm?>(initialValue = null) {
        value = repository.getAlgorithmById(algorithmId)
    }

    // CŒUR EN TEMPS RÉEL
    var isFavorite by remember { mutableStateOf(FavoriteManager.isFavorite(context, algorithmId)) }

    if (algorithm == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val algo = algorithm!!
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pseudo-code", "Python", "Exemple")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(algo.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        if (isFavorite) {
                            FavoriteManager.addFavorite(context, algorithmId)
                        } else {
                            FavoriteManager.removeFavorite(context, algorithmId)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(algo.description, modifier = Modifier.padding(16.dp))
            Spacer(Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> CodeBlock(algo.pseudocode)
                1 -> CodeBlock(algo.python)
                2 -> ExampleBlock(algo.exampleInput, algo.exampleOutput)
            }

            Button(
                onClick = { navController.navigate("execute/$algorithmId") },
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text("Exécuter en Python")
            }
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                text = code
                typeface = Typeface.MONOSPACE
                setTextIsSelectable(true)
                setPadding(40, 40, 40, 40)
                background = ColorDrawable(Color.parseColor("#1E1E1E"))
                setTextColor(Color.WHITE)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ExampleBlock(input: String, output: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exemple d’entrée :", style = MaterialTheme.typography.titleMedium)
        Text(input, modifier = Modifier.padding(8.dp))
        Spacer(Modifier.height(12.dp))
        Text("Sortie attendue :", style = MaterialTheme.typography.titleMedium)
        Text(output, modifier = Modifier.padding(8.dp))
    }
}