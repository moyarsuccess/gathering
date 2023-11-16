package com.gathering.android.event.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gathering.android.event.Event
import com.gathering.android.ui.theme.CustomBackgroundColor

@Composable
fun CustomSnackbar(
    deletedEvent: Event?,
    onUndoDeleteEvent: (event: Event) -> Unit
) {
    var deletedEvent1 by remember { mutableStateOf(deletedEvent) }

    deletedEvent1?.let { event ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            backgroundColor = CustomBackgroundColor,
            action = {
                TextButton(
                    onClick = {
                        deletedEvent1 = null
                        onUndoDeleteEvent(event)
                    }
                ) {
                    Text(
                        "Undo",
                        modifier = Modifier,
                    )
                }
            }
        ) {
            Text(
                "${event.eventName} deleted",
                modifier = Modifier,
            )
        }
    }
}