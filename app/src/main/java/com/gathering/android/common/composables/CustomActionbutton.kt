package com.gathering.android.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun CustomActionButtonPreview() {
    CustomActionButton(
        isLoading = false,
        text = "Button",
        onClick = {},
        modifier = Modifier,
    )
}

@Composable
fun CustomActionButton(
    isLoading: Boolean?,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            shape = RoundedCornerShape(0.dp),
            onClick = onClick,
            modifier = modifier,
            colors = colors,
            contentPadding = PaddingValues(8.dp),
            content = {
                if (isLoading == true) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
        )
    }
}