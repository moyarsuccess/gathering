package com.gathering.android.event.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gathering.android.R
import com.gathering.android.event.Event
import com.gathering.android.ui.theme.customBackgroundColor

@Composable
fun EventImage(event: Event) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = event.photoUrl)
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(com.google.android.material.R.drawable.mtrl_ic_error)
            }).build()
    )
    Card(colors = CardDefaults.cardColors(customBackgroundColor)) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .size(170.dp)
        )
    }
}

@Composable
fun EventImage(imageUrl: String) {
    Card(Modifier.border(2.dp, Color.Gray, RoundedCornerShape(10.dp)))
    {
        val painter = if (imageUrl.isEmpty()) {
            painterResource(id = R.drawable.ic_launcher_foreground)
        } else {
            rememberAsyncImagePainter(model = imageUrl)
        }
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "EventPhoto",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}