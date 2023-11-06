package com.gathering.android.event.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.gathering.android.event.Event

@Composable
fun FavoriteIcon(event: Event, onFavClick: (Event) -> Unit) {
    IconButton(
        onClick = { onFavClick(event) },
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
    ) {
        Icon(
            painter = rememberVectorPainter(
                if (event.liked) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                }
            ),
            contentDescription = "thumb",
        )
    }
}