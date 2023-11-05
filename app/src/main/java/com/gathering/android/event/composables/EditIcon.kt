package com.gathering.android.event.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gathering.android.event.Event

@Composable
fun EditIcon(event: Event, onEditClick: (Event) -> Unit) {
    IconButton(
        onClick = { onEditClick(event) }
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp)
                .size(30.dp)
        )
    }
}