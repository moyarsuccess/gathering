package com.gathering.android.common.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isNoData: Boolean = false,
) {
    Button(
        shape = RoundedCornerShape(0.dp),
        onClick = { },
        modifier = modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.DarkGray,
                strokeWidth = 5.dp
            )
        }

        if (isNoData) {
            Text(
                text = text,
                color = Color.Gray
            )
        }
    }
}