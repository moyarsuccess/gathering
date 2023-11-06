package com.gathering.android.common.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun UnderlinedButton(text: String, onClick: () -> Unit) {
    val underlinedText = remember(text) {
        AnnotatedString.Builder(text).apply {
            addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = 0,
                end = text.length
            )
        }.toAnnotatedString()
    }
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black,
        )
    ) {
        Text(
            text = underlinedText
        )
    }
}