package com.samaali.codememo.ui.screens

import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
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

private const val TAG = "JDoodleDebug"

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
    var codeToExecute by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // ================== CLÉS JDOODLE ==================
    val CLIENT_ID = "15dcb68d04b24c7f9adb9827ed508de"
    val CLIENT_SECRET = "4f58d4dc94020285dcb4379b6dae5e99761a459d51479552dfa4f79b6299c567"

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
                    label = { Text("Entrées (stdin)") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    placeholder = { Text("Ex: valeur pour input()") }
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
                            color = if (output.contains("Error") || output.contains("Erreur")) Color.Red else Color.Green,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (!isExecuting && codeToExecute.isNotBlank()) {
                        isExecuting = true
                        output = "Exécution en cours...\n"

                        scope.launch {
                            val (result, credit) = executePythonJDoodle(
                                code = codeToExecute,
                                userInput = userInstructions,
                                clientId = CLIENT_ID,
                                clientSecret = CLIENT_SECRET
                            )

                            output = result

                            if (credit >= 0) {
                                output += "\n\n────────────────────\n"
                                output += "Crédit restant : $credit"
                            }
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

// ====================== FONCTION JDOODLE (Version Corrigée) ======================

suspend fun executePythonJDoodle(
    code: String,
    userInput: String = "",
    clientId: String,
    clientSecret: String
): Pair<String, Int> {

    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        // NE PAS utiliser trimIndent() - garder l'indentation originale
        // Si le code est une fonction sans appel, on peut ajouter un appel de test
        var finalCode = code

        // Option: Détecter si c'est une fonction et ajouter un exemple d'appel
        if (code.contains("def ") && !code.contains("if __name__") && !code.contains("print(")) {
            // C'est probablement une fonction sans appel
            // On peut ajouter un appel de test si userInput contient des paramètres
            if (userInput.isNotBlank()) {
                // Si l'utilisateur a fourni des paramètres, on les utilise
                finalCode += "\n\n# Test avec les paramètres fournis\n"
                finalCode += "print($userInput)"
            } else {
                // Sinon, on ajoute un message
                finalCode += "\n\n# Fonction définie mais non appelée\n"
                finalCode += "# Pour exécuter, ajoutez un appel comme: print(est_premier(7))"
            }
        }

        val jsonBody = JSONObject().apply {
            put("script", finalCode)
            put("language", "python3")
            put("versionIndex", "4")
            put("stdin", userInput)  // userInput c'est pour input() dans le code Python
            put("clientId", clientId)
            put("clientSecret", clientSecret)
        }

        Log.d(TAG, "Code final exécuté: $finalCode")
        Log.d(TAG, "stdin fourni: $userInput")

        val request = Request.Builder()
            .url("https://api.jdoodle.com/v1/execute")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.d(TAG, "Réponse brute: $body")

                if (!response.isSuccessful) {
                    return@withContext Pair("Erreur serveur (${response.code}): $body", -1)
                }

                val jsonResponse = JSONObject(body)
                val output = jsonResponse.optString("output", "")
                val error = jsonResponse.optString("error", "")
                val statusCode = jsonResponse.optInt("statusCode", 0)

                // Construire le résultat
                val resultText = when {
                    error.isNotBlank() && error != "null" -> "Erreur d'exécution:\n$error"
                    output.isNotBlank() -> output
                    else -> {
                        if (finalCode.contains("def ") && !finalCode.contains("print(")) {
                            "⚠️ Le code contient une fonction mais elle n'est pas appelée.\n\n" +
                                    "Pour exécuter la fonction, vous pouvez :\n" +
                                    "1. Modifier le code pour ajouter un appel\n" +
                                    "2. Utiliser le champ 'Entrées (stdin)' pour passer des paramètres\n\n" +
                                    "Exemple d'appel : print(est_premier(7))"
                        } else {
                            "Exécution terminée (aucune sortie)"
                        }
                    }
                }

                val credit = jsonResponse.optInt("creditRemaining", -1)
                Pair(resultText, credit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur réseau", e)
            Pair("Erreur réseau : ${e.message}", -1)
        }
    }
}


// ====================== PythonCodeView ======================
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
            val spannable = SpannableString(code.ifEmpty { "# Aucun code à exécuter" })
            val keywords = listOf("def", "class", "return", "if", "else", "elif", "for", "while", "in", "import", "from", "as", "try", "except", "finally", "with", "True", "False", "None", "and", "or", "not", "pass", "break", "continue", "print", "input")

            keywords.forEach { kw ->
                var start = code.indexOf(kw)
                while (start != -1) {
                    val end = start + kw.length
                    if ((start == 0 || !code[start - 1].isLetterOrDigit()) && (end == code.length || !code[end].isLetterOrDigit())) {
                        spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#d656ad")), start, end, 0)
                    }
                    start = code.indexOf(kw, start + 1)
                }
            }

            Regex("""(".*?")|('.*?')""").findAll(code).forEach {
                spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#CE9178")), it.range.first, it.range.last + 1, 0)
            }

            Regex("#.*").findAll(code).forEach {
                spannable.setSpan(ForegroundColorSpan(AndroidColor.parseColor("#57A64A")), it.range.first, it.range.last + 1, 0)
            }

            view.text = spannable
        },
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    )
}