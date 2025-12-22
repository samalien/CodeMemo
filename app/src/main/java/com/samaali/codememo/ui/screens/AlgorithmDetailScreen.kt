package com.samaali.codememo.ui.screens

import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val coroutineScope = rememberCoroutineScope()

    var algorithm by remember { mutableStateOf<Algorithm?>(null) }
    var userExercise by remember { mutableStateOf<UserExercise?>(null) }

    // UN SEUL LaunchedEffect qui gère la priorité
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // 1. Priorité à l'exercice personnalisé
            if (userExerciseId != null) {
                val allExos = userRepo.getAll().first()
                val exo = allExos.find { it.id == userExerciseId }
                if (exo != null) {
                    userExercise = exo
                    return@launch  // On sort : on a trouvé l'exo perso → pas besoin de charger l'algo
                }
            }

            // 2. Si pas d'exo perso (ou pas trouvé), on charge l'algo standard
            if (algorithmId != null) {
                algorithm = algoRepo.getAlgorithmById(algorithmId)
            }
        }
    }

    // Loading
    if (algorithm == null && userExercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isUserExercise = userExercise != null
    val itemName = if (isUserExercise) userExercise!!.name else algorithm!!.name
    val itemDescription = if (isUserExercise) userExercise!!.description else algorithm!!.description
    val itemPseudocode = if (isUserExercise) userExercise!!.pseudocode else algorithm!!.pseudocode
    val itemPython = if (isUserExercise) userExercise!!.python else algorithm!!.python
    val itemExampleInput = if (isUserExercise) userExercise!!.exampleInput else algorithm!!.exampleInput
    val itemExampleOutput = if (isUserExercise) userExercise!!.exampleOutput else algorithm!!.exampleOutput

    var isFavorite by remember { mutableStateOf(false) }
    val currentId = algorithmId ?: userExerciseId ?: 0

    // Favoris uniquement pour les algos standards
    if (!isUserExercise && algorithmId != null) {
        isFavorite = FavoriteManager.isFavorite(context, algorithmId)
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pseudo-code", "Python", "Exemple")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itemName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (!isUserExercise && algorithmId != null) {
                        IconButton(
                            onClick = {
                                isFavorite = !isFavorite
                                if (isFavorite) {
                                    FavoriteManager.addFavorite(context, currentId)
                                } else {
                                    FavoriteManager.removeFavorite(context, currentId)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = itemDescription,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                when (selectedTab) {
                    0 -> CodeBlock(itemPseudocode, isPython = false)
                    1 -> CodeBlock(itemPython, isPython = true)
                    2 -> ExampleBlock(itemExampleInput, itemExampleOutput)
                }
            }

            Button(
                onClick = {
                    navController.navigate("execute/$currentId")
                },
                modifier = Modifier.align(Alignment.End).padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Exécuter en Python", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CodeBlock(code: String, isPython: Boolean = true) {
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
                                spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#d656ad")), start, end, 0)
                            }
                            start = code.indexOf(keyword, start + 1)
                        }
                    }
                    val stringRegex = Regex("""(".*?")|('.*?')""")
                    stringRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#CE9178")), match.range.first, match.range.last + 1, 0)
                    }
                    val commentRegex = Regex("#.*")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }
                } else {
                    val pseudoKeywords = listOf("Procédure", "POUR", "FIN POUR", "SI", "ALORS", "SINON", "FIN SI", "TANT QUE", "FIN TANT QUE", "RETOURNER", "FONCTION", "DEBUT", "FIN", "VARIABLES", "CONSTANTES")
                    pseudoKeywords.forEach { keyword ->
                        var start = code.indexOf(keyword, ignoreCase = true)
                        while (start != -1) {
                            val end = start + keyword.length
                            spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#d656ad")), start, end, 0)
                            start = code.indexOf(keyword, start + 1, ignoreCase = true)
                        }
                    }
                    val commentRegex = Regex("(//.*)|(#.*)")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }
                    val stringRegex = Regex("""(".*?")|('.*?')""")
                    stringRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#CE9178")), match.range.first, match.range.last + 1, 0)
                    }
                }

                text = spannable
                typeface = Typeface.MONOSPACE
                setTextIsSelectable(true)
                setPadding(40, 40, 40, 40)
                background = ColorDrawable(AndroidColor.parseColor("#1E1E1E"))
                setTextColor(AndroidColor.WHITE)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ExampleBlock(input: String, output: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exemple d’entrée :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(input, modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 12.dp))

        Text("Sortie attendue :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(output, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
    }
}