package com.samaali.codememo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.samaali.codememo.ui.navigation.AppNavGraph
import com.samaali.codememo.ui.theme.CodeMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeMemoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    // 🚀 LANCEMENT DE L’APP NAVIGATION
                    AppNavGraph(navController = navController)
                }
            }
        }

    }
}
