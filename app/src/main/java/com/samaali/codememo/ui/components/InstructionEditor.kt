package com.samaali.codememo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Column

@Composable
fun InstructionEditor(
    instructions: String,
    onInstructionsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Instructions / Appel de fonction :",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = instructions,
            onValueChange = onInstructionsChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E), shape = MaterialTheme.shapes.small)
                .padding(16.dp)
                .height(150.dp)  // Hauteur ajustable
        ) { innerTextField ->
            if (instructions.isEmpty()) {
                Text(
                    "Ex : resultat = tri_bulles([64, 34, 25, 12])\nprint(resultat)",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            innerTextField()
        }
    }
}