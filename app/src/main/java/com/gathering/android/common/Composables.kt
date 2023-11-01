package com.gathering.android.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.gathering.android.R

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
        colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
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