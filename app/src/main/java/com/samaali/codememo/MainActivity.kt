package com.samaali.codememo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.samaali.codememo.ui.auth.AuthManager
import com.samaali.codememo.ui.navigation.AppNavGraph
import com.samaali.codememo.ui.theme.CodeMemoTheme
import androidx.compose.foundation.layout.fillMaxSize // AJOUTEZ CETTE LIGNE
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INITIALISATION FIREBASE / GOOGLE SIGN-IN
        AuthManager.init(this)

        setContent {
            CodeMemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}