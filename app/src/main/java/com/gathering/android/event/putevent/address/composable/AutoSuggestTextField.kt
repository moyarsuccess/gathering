package com.gathering.android.event.putevent.address.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun AutoSuggestTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    onItemClicked: (String) -> Unit,
    onDismissed: () -> Unit,
    onClearClick: () -> Unit,
    expanded: Boolean,
    list: List<String>,
    label: String = ""
) {
    Box(modifier) {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onDismissed()
                    }
                },
            value = TextFieldValue(value, TextRange(value.length)),
            onValueChange = { onValueChanged(it.text) },
            label = { Text(label) },
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            trailingIcon = {
                IconButton(onClick = { onClearClick() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                }
            }
        )
        DropdownMenu(
            modifier = Modifier.padding(10.dp),
            expanded = expanded,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = onDismissed,
        ) {
            list.forEach { text ->
                DropdownMenuItem(onClick = {
                    onItemClicked(text)
                }) {
                    Text(text = text)
                }
            }
        }
    }
}