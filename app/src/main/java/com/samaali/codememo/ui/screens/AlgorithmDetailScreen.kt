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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    var isFavorite by remember(algorithmId) {
        mutableStateOf(FavoriteManager.isFavorite(context, algorithmId))
    }

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
                title = { Text(algo.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = algo.description,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                when (selectedTab) {
                    0 -> CodeBlock(algo.pseudocode, isPython = false)
                    1 -> CodeBlock(algo.python, isPython = true)
                    2 -> ExampleBlock(algo.exampleInput, algo.exampleOutput)
                }
            }

            Button(
                onClick = { navController.navigate("execute/$algorithmId") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp),
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
                    // Python : mots-clés en bleu
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

                    // Strings en orange
                    val stringRegex = Regex("""(".*?")|('.*?')""")
                    stringRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#CE9178")), match.range.first, match.range.last + 1, 0)
                    }

                    // Commentaires en vert
                    val commentRegex = Regex("#.*")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }
                } else {
                    // Pseudo-code français : coloration personnalisée
                    val pseudoKeywords = listOf("Procédure", "POUR", "FIN POUR", "SI", "ALORS", "SINON", "FIN SI", "TANT QUE", "FIN TANT QUE", "RETOURNER", "FONCTION", "DEBUT", "FIN", "VARIABLES", "CONSTANTES")
                    pseudoKeywords.forEach { keyword ->
                        var start = code.indexOf(keyword, ignoreCase = true)
                        while (start != -1) {
                            val end = start + keyword.length
                            spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#d656ad")), start, end, 0) // Bleu pour les mots-clés
                            start = code.indexOf(keyword, start + 1, ignoreCase = true)
                        }
                    }

                    // Commentaires (// ou #)
                    val commentRegex = Regex("(//.*)|(#.*)")
                    commentRegex.findAll(code).forEach { match ->
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), match.range.first, match.range.last + 1, 0)
                    }

                    // Chaînes entre " " ou ' '
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