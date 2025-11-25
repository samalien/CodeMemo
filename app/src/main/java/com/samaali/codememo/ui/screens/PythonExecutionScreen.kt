package com.samaali.codememo.ui.screen

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.samaali.codememo.data.model.Algorithm
import com.samaali.codememo.data.repository.AlgorithmRepository

@Composable
fun PythonExecutionScreen(algorithmId: Int) {
    val context = LocalContext.current
    val repository = remember { AlgorithmRepository(context) }

    var isLoading by remember { mutableStateOf(true) }

    val algorithm by produceState<Algorithm?>(initialValue = null) {
        value = repository.getAlgorithmById(algorithmId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading || algorithm == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.allowFileAccess = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccessFromFileURLs = true
                        settings.allowUniversalAccessFromFileURLs = true

                        // Interface JavaScript pour récupérer la sortie
                        addJavascriptInterface(PythonOutputInterface { output ->
                            // Tu peux afficher ça dans un Text() si tu veux
                            println("Python Output: $output")
                        }, "AndroidOutput")

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                val safeCode = algorithm!!.python
                                    .replace("`", "\\`")
                                    .replace("$", "\\$")
                                    .replace("\n", "\\n")
                                    .replace("\r", "")

                                // Injecte le code Python dans l’éditeur
                                view?.evaluateJavascript("setCode(`$safeCode`)", null)
                            }
                        }

                        // Charge ton éditeur Pyodide
                        loadUrl("file:///android_asset/pyodide/editor.html")
                    }
                }
            )
        }
    }
}

// Interface pour récupérer la sortie Python depuis JS
class PythonOutputInterface(private val callback: (String) -> Unit) {
    @JavascriptInterface
    fun showOutput(output: String) {
        callback(output)
    }
}