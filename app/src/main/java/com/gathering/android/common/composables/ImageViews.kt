package com.gathering.android.common.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.gathering.android.R

@Composable
fun CircularImageView(bmp: Bitmap?, size: Dp) {
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
fun CircularImageView(imageUri: String?, size: Dp, onClick: () -> Unit) {
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