package com.samaali.codememo.ui.screen

import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.samaali.codememo.data.repository.AlgorithmRepository
import com.samaali.codememo.data.repository.UserExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonExecutionScreen(
    algorithmId: Int? = null,
    userExerciseId: Int? = null,
    navController: NavController
) {
    val context = LocalContext.current
    val algoRepo = remember { AlgorithmRepository(context) }
    val userRepo = remember { UserExerciseRepository(context) }
    val scope = rememberCoroutineScope()

    var isExecuting by remember { mutableStateOf(false) }
    var output by remember { mutableStateOf("Prêt pour l'exécution...") }
    var userInstructions by remember { mutableStateOf("") }
    var codeToExecute by remember { mutableStateOf<String>("") } // Chaîne vide = pas encore chargé
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(algorithmId, userExerciseId) {
        scope.launch {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                var foundCode: String? = null
                if (userExerciseId != null) {
                    val allExos = userRepo.getAll().first()
                    val exo = allExos.find { it.id == userExerciseId }
                    if (exo != null) foundCode = exo.python.orEmpty()
                }
                if (foundCode == null && algorithmId != null) {
                    val algo = algoRepo.getAlgorithmById(algorithmId)
                    foundCode = algo?.python.orEmpty()
                }
                foundCode ?: ""
            }
            codeToExecute = result
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Exécuteur Python") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
            }
        )

        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Code source :", style = MaterialTheme.typography.labelLarge)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else if (codeToExecute.isBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Aucun code Python disponible",
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        PythonCodeView(code = codeToExecute)
                    }
                }

                OutlinedTextField(
                    value = userInstructions,
                    onValueChange = { userInstructions = it },
                    label = { Text("Appel de la fonction pour test") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    placeholder = { Text("Ex: 5\n10\npour input() multiples") }
                )

                Spacer(Modifier.height(16.dp))

                Text("Console :", fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = output,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            color = if (output.contains("Erreur", ignoreCase = true)) Color.Red else Color.Green,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (!isExecuting && codeToExecute.isNotBlank()) {
                        isExecuting = true
                        output = "Exécution en cours..."
                        scope.launch {
                            output = executePythonRemote("$codeToExecute\n$userInstructions")
                            isExecuting = false
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = if (isExecuting || codeToExecute.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Exécuter", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun PythonCodeView(code: String) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                typeface = Typeface.MONOSPACE
                setTextIsSelectable(true)
                setPadding(40, 40, 40, 40)
                background = ColorDrawable(AndroidColor.parseColor("#1E1E1E"))
                setTextColor(AndroidColor.WHITE)
            }
        },
        update = { view ->
            // ← C'EST ICI QUE LA COLORATION EST APPLIQUÉE À CHAQUE CHANGEMENT
            val spannable = SpannableString(code.ifEmpty { "# Aucun code à exécuter" })

            // Mots-clés Python
            val keywords = listOf(
                "def", "class", "return", "if", "else", "elif", "for", "while", "in",
                "import", "from", "as", "try", "except", "finally", "with",
                "True", "False", "None", "and", "or", "not", "pass", "break",
                "continue", "lambda", "yield", "assert", "raise", "global", "nonlocal"
            )

            keywords.forEach { kw ->
                var start = code.indexOf(kw)
                while (start != -1) {
                    val end = start + kw.length
                    if ((start == 0 || !code[start - 1].isLetterOrDigit()) &&
                        (end == code.length || !code[end].isLetterOrDigit())
                    ) {
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#d656ad")), start, end, 0)
                    }
                    start = code.indexOf(kw, start + 1)
                }
            }

            // Chaînes
            Regex("""(".*?")|('.*?')""").findAll(code).forEach {
                spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#CE9178")), it.range.first, it.range.last + 1, 0)
            }

            // Commentaires
            Regex("#.*").findAll(code).forEach {
                spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), it.range.first, it.range.last + 1, 0)
            }

            view.text = spannable
        },
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
}

// Fonction d'exécution (inchangée, juste un petit message plus clair)
suspend fun executePythonRemote(code: String): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("language", "python")
            put("version", "3.10.0")
            put("files", org.json.JSONArray().put(JSONObject().apply { put("content", code) }))
        }
        val request = Request.Builder()
            .url("https://emkc.org/api/v2/piston/execute")   // ← Alternative plus stable
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val data = response.body?.string()
                if (response.isSuccessful && data != null) {
                    val run = JSONObject(data).getJSONObject("run")
                    val err = run.getString("stderr")
                    if (err.isNotEmpty()) "Erreur :\n$err" else run.getString("stdout").ifEmpty { "Exécution réussie (aucune sortie)" }
                } else "Erreur serveur"
            }
        } catch (e: Exception) {
            "Erreur réseau : ${e.message}"
        }
    }
}