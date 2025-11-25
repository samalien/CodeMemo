package com.samaali.codememo.ui.screens

import android.graphics.Typeface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun AlgorithmDetailScreen(algorithmId: Int, navController: NavController) {

    // 👉 LocalContext.current fonctionne maintenant
    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }

    val algorithm by produceState<Algorithm?>(initialValue = null) {
        value = repository.getAlgorithmById(algorithmId)
    }

    if (algorithm != null) {
        val algo = algorithm!!
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Pseudo-code", "Python", "Exemple")

        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = algo.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = algo.description,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> CodeBlock(algo.pseudocode)
                1 -> CodeBlock(algo.python)
                2 -> ExampleBlock(algo.exampleInput, algo.exampleOutput)
            }

            Button(
                onClick = { navController.navigate("execute/$algorithmId") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exécuter en Python")
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                text = code
                typeface = Typeface.MONOSPACE
                setTextIsSelectable(true)
                setPadding(40, 40, 40, 40)
                background = ColorDrawable(Color.parseColor("#1E1E1E"))
                setTextColor(Color.WHITE)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun ExampleBlock(input: String, output: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exemple d’entrée :", style = MaterialTheme.typography.titleMedium)
        Text(input, modifier = Modifier.padding(8.dp))

        Spacer(modifier = Modifier.height(12.dp))

        Text("Sortie attendue :", style = MaterialTheme.typography.titleMedium)
        Text(output, modifier = Modifier.padding(8.dp))
    }
}
