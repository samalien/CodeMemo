package com.samaali.codememo.ui.screens

import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.model.UserExercise
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.data.repository.UserExerciseRepository
import com.samaali.codememo.ui.utils.FavoriteManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmDetailScreen(
    algorithmId: Int? = null,
    userExerciseId: Int? = null,
    navController: NavController
) {
    val context = LocalContext.current
    val algoRepo = remember { AlgorithmRepository(context) }
    val userRepo = remember { UserExerciseRepository(context) }
    val scope = rememberCoroutineScope()

    var algorithm by remember { mutableStateOf<Algorithm?>(null) }
    var userExercise by remember { mutableStateOf<UserExercise?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            if (userExerciseId != null) {
                val exo = userRepo.getAll().first().find { it.id == userExerciseId }
                if (exo != null) { userExercise = exo; return@launch }
            }
            if (algorithmId != null) algorithm = algoRepo.getAlgorithmById(algorithmId)
        }
    }

    if (algorithm == null && userExercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val isUserExercise = userExercise != null
    val itemName = if (isUserExercise) userExercise!!.name else algorithm!!.name
    val itemDesc = if (isUserExercise) userExercise!!.description else algorithm!!.description

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(itemName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
            }
        )

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(itemDesc, modifier = Modifier.padding(16.dp))
            }

            TabRow(selectedTabIndex = selectedTab) {
                listOf("Pseudo-code", "Python", "Exemple").forEachIndexed { i, t ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(t) })
                }
            }

            Box(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> CodeBlock(if (isUserExercise) userExercise!!.pseudocode else algorithm!!.pseudocode)
                    1 -> CodeBlock(if (isUserExercise) userExercise!!.python else algorithm!!.python)
                    2 -> ExampleBlock(
                        if (isUserExercise) userExercise!!.exampleInput else algorithm!!.exampleInput,
                        if (isUserExercise) userExercise!!.exampleOutput else algorithm!!.exampleOutput
                    )
                }
            }

            Button(
                onClick = { navController.navigate("execute/${algorithmId ?: userExerciseId}") },
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, null)
                Text("Tester le code")
            }
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                text = SpannableString(code)
                typeface = Typeface.MONOSPACE
                setPadding(40, 40, 40, 40)
                setTextColor(android.graphics.Color.WHITE)
                background = ColorDrawable(android.graphics.Color.parseColor("#1E1E1E"))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ExampleBlock(input: String, output: String) {
    Column {
        Text("Entrée :", fontWeight = FontWeight.Bold)
        Text(input, modifier = Modifier.padding(bottom = 8.dp))
        Text("Sortie :", fontWeight = FontWeight.Bold)
        Text(output)
    }
}