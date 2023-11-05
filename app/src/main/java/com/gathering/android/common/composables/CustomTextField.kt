package com.gathering.android.common.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    onClicked: () -> Unit = {},
    maxLine: Int = 1,
    label: String,
    enabled: Boolean = true,
) {
    val textFieldValueFun = { textValue: TextFieldValue ->
        onValueChange(textValue.text)
    }
    OutlinedTextField(
        value = TextFieldValue(
            value,
            selection = TextRange(value.length),
        ),
        onValueChange = textFieldValueFun,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        maxLines = maxLine,
        label = { Text(label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Black
        ),
        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release) {
                        onClicked()
                    }
                }
            }
        },
        enabled = enabled
    )
}