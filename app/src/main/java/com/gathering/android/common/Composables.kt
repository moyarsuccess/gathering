package com.gathering.android.common

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.gathering.android.R

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
    isLoading: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.DarkGray
    )
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
            modifier = modifier
                .height(60.dp)
                .width(170.dp),
            colors = colors,
            contentPadding = PaddingValues(8.dp),
            content = {
                Text(
                    text = text,
                    modifier = Modifier.padding(4.dp),
                )
            }
        )
    }
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

@Composable
@Preview(showBackground = true)
fun ErrorTextPreview() {
    ErrorText(error = "error")
}

@Composable
fun ErrorText(error: String) {
    Text(
        text = error,
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 17.sp, color = Color.Red
        )
    )
}

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

@Composable
fun NavigationBarPaddingSpacer() {
    Spacer(
        modifier = Modifier
            .height(50.dp)
            .navigationBarsPadding()
    )
}

@Composable
fun ShowText(
    text: String,
    modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = text
    )
}

@Composable
fun ImageView(imageUri: String?, size: Dp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(25.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        val painter = if (imageUri.isNullOrEmpty()) {
            painterResource(id = R.drawable.ic_person)
        } else {
            rememberAsyncImagePainter(model = imageUri)
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .size(size)
                .background(Color.White, CircleShape)
                .clickable { onClick() }
        )
    }
}

@Composable
fun ImageView(bmp: Bitmap?, size: Dp) {
    Card(
        modifier = Modifier
            .padding(25.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        val painter = if (bmp == null) {
            painterResource(id = R.drawable.ic_person)
        } else {
            rememberAsyncImagePainter(model = bmp)
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .size(size)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun HorizontalDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    )
}