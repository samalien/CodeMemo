package com.samaali.codememo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.samaali.codememo.ui.navigation.AppNavGraph
import com.samaali.codememo.ui.theme.CodeMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeMemoTheme {
                Surface {
                    AppNavGraph()
                }
            }
        }
    }
}