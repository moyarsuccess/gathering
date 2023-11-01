package com.gathering.android.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.R


@Composable
@Preview
fun AuthButtonPreview() {
    AuthButton(text = "Button", onClick = { /*TODO*/ })
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.DarkGray
    )
) {
    Button(
        shape = RoundedCornerShape(0.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(text)
        }
    }
}

@Composable
fun CustomUnderlinedButton(text: String, onClick: () -> Unit) {
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

@Composable
fun GatheringEmailTextField(
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            focusedLabelColor = Color.Gray,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
}

@Composable
fun GatheringPasswordTextField(
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            focusedLabelColor = Color.Gray,
        )
    )
}
@Composable
@Preview(showBackground = true)
fun CustomTextViewPreview() {
    CustomTextView(textResId = R.string.fragment_1_text)
}

@Composable
fun CustomTextView(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    textStyle: TextStyle = TextStyle.Default
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        val text = stringResource(id = textResId)
        Text(
            text = text,
            style = textStyle,
            modifier = modifier
        )
    }
}