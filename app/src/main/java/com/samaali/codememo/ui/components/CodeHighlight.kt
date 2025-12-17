package com.samaali.codememo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme

@Composable
fun CodeHighlight(
    code: String,
    language: CodeLang = CodeLang.Python,
    modifier: Modifier = Modifier
) {
    val parser = remember { PrettifyParser() }
    val theme = remember { CodeThemeType.Monokai.theme() }  // Thème sombre Python parfait

    val highlighted: AnnotatedString = remember(code) {
        parseCodeAsAnnotatedString(
            parser = parser,
            theme = theme,
            lang = language,
            code = code
        )
    }

    Text(
        text = highlighted,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
    )
}