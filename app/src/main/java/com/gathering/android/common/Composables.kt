package com.gathering.android.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    TextField(
        shape = RoundedCornerShape(5.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .padding(10.dp),
        colors = TextFieldDefaults
            .textFieldColors(containerColor = Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,

    ) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(modifier = Modifier
        .fillMaxWidth()
        .clickable { }
        .padding(10.dp),
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
    )
}


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null
) {
    Button(
        shape = RoundedCornerShape(0.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(2f)
            .padding(top = 30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.DarkGray
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(text)
        }
    }

    if (error != null) {
        Text(
            text = error,
            color = Color.Red
        )
    }
}

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


