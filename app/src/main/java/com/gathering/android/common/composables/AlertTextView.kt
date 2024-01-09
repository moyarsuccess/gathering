package com.gathering.android.common.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.ui.theme.Green

@Composable
@Preview(showBackground = true)
fun ErrorTextViewPreview() {
    AlertTextView(msg = "error")
}

@Composable
fun AlertTextView(msg: String) {
    Text(
        text = msg,
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 17.sp, color = Green
        )
    )
}