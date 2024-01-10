package com.gathering.android.event.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FabButton(
    onFabClick: () -> Unit
) {
    val customBackgroundColor = Color(0xFFEEEBEB)
    FloatingActionButton(
        onClick = {
            onFabClick()
        },
        modifier = Modifier
            .padding(16.dp)
            .size(56.dp),
        backgroundColor = customBackgroundColor
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
        )
    }
}