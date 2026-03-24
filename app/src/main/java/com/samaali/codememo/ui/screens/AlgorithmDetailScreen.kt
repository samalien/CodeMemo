package com.samaali.codememo.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.samaali.codememo.ui.navigation.Screen
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
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val isUserExercise = userExerciseId != null
    val currentId = algorithmId ?: userExerciseId ?: 0

    LaunchedEffect(Unit) {
        scope.launch {
            if (userExerciseId != null) {
                val exo = userRepo.getAll().first().find { it.id == userExerciseId }
                if (exo != null) {
                    userExercise = exo
                    isFavorite = FavoriteManager.isFavorite(context, currentId, isUserExercise = true)
                    return@launch
                }
            }
            if (algorithmId != null) {
                algorithm = algoRepo.getAlgorithmById(algorithmId)
                isFavorite = FavoriteManager.isFavorite(context, currentId, isUserExercise = false)
            }
        }
    }

    if (algorithm == null && userExercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val itemName = if (isUserExercise) userExercise!!.name else algorithm!!.name
    val itemDesc = if (isUserExercise) userExercise!!.description else algorithm!!.description

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(itemName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch {
                            FavoriteManager.toggleFavorite(context, currentId, isUserExercise)
                            isFavorite = !isFavorite
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                    )
                }
            }
        )

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(itemDesc, modifier = Modifier.padding(16.dp))
            }

            TabRow(selectedTabIndex = selectedTab) {
                listOf("Algorithme", "Python", "Exemple").forEachIndexed { i, t ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(t) })
                }
            }

            Box(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> CodeBlock(
                        code = if (isUserExercise) userExercise!!.pseudocode else algorithm!!.pseudocode,
                        isPython = false
                    )
                    1 -> CodeBlock(
                        code = if (isUserExercise) userExercise!!.python else algorithm!!.python,
                        isPython = true
                    )
                    2 -> ExampleBlock(
                        input = if (isUserExercise) userExercise!!.exampleInput else algorithm!!.exampleInput,
                        output = if (isUserExercise) userExercise!!.exampleOutput else algorithm!!.exampleOutput
                    )
                }
            }

            Button(
                onClick = {
                    when {
                        algorithmId != null -> navController.navigate(Screen.AlgoExecute.createRoute(algorithmId))
                        userExerciseId != null -> navController.navigate(Screen.UserExecute.createRoute(userExerciseId))
                    }
                },
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Text("Tester le code")
            }
        }
    }
}

// Tes fonctions CodeBlock et ExampleBlock restent exactement comme dans ton dernier code
// (je les garde ici pour complétude)

@Composable
fun CodeBlock(code: String, isPython: Boolean) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                val spannable = SpannableString(code)
                if (isPython) {
                    val pythonKeywords = listOf("def", "class", "return", "if", "else", "elif", "for", "while", "in", "import", "from", "as", "try", "except", "finally", "with", "True", "False", "None", "and", "or", "not")
                    pythonKeywords.forEach { keyword ->
                        var start = code.indexOf(keyword)
                        while (start != -1) {
                            val end = start + keyword.length
                            if ((start == 0 || !code[start - 1].isLetterOrDigit()) && (end == code.length || !code[end].isLetterOrDigit())) {
                                spannable.setSpan(ForegroundColorSpan(Color.parseColor("#d656ad")), start, end, 0)
                            }
                            start = code.indexOf(keyword, start + 1)
                        }
                    }
                    val stringRegex = Regex("""(".*?")|('.*?')""")
                    stringRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#CE9178")), match.range.first, match.range.last + 1, 0)
                    }
                    val commentRegex = Regex("#.*")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }
                } else {
                    val pseudoKeywords = listOf(
                        "Procédure", "Procedure", "Fonction", "Function",
                        "POUR", "Pour", "FOR", "Fin Pour", "FIN POUR", "End For",
                        "SI", "Si", "IF", "ALORS", "Alors", "THEN", "SINON", "Sinon", "ELSE",
                        "FIN SI", "Fin Si", "END IF",
                        "TANT QUE", "Tant que", "WHILE", "FIN TANT QUE", "Fin Tant Que", "END WHILE",
                        "RETOURNER", "Retourner", "RETURN",
                        "DEBUT", "Début", "BEGIN", "FIN", "Fin", "END",
                        "VARIABLES", "Variables", "CONSTANTES", "Constantes", "FinSi", "FinPour", "FinTantQue"
                    )
                    pseudoKeywords.forEach { keyword ->
                        var start = code.indexOf(keyword, ignoreCase = true)
                        while (start != -1) {
                            val end = start + keyword.length
                            if ((start == 0 || !code[start - 1].isLetterOrDigit()) && (end == code.length || !code[end].isLetterOrDigit())) {
                                spannable.setSpan(ForegroundColorSpan(Color.parseColor("#d656ad")), start, end, 0)
                            }
                            start = code.indexOf(keyword, start + 1, ignoreCase = true)
                        }
                    }
                    val commentRegex = Regex("(//.*)|(#.*)")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }
                    val stringRegex = Regex("""(".*?")|('.*?')""")
                    stringRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#CE9178")), match.range.first, match.range.last + 1, 0)
                    }
                }
                text = spannable
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
    Column {
        Text("Entrée :", fontWeight = FontWeight.Bold)
        Text(input, modifier = Modifier.padding(bottom = 8.dp))
        Text("Sortie :", fontWeight = FontWeight.Bold)
        Text(output)
    }
}