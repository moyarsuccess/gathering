package com.gathering.android.event.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gathering.android.event.Event

@Composable
fun EventItem(
    showFavoriteIcon: Boolean,
    event: Event?,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event?) -> Unit,
    onFavClick: (Event?) -> Unit,
    showEditIcon: Boolean
) {
    Card(
        modifier = Modifier
            .clickable {
                event?.let { onItemClick(it) }
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            Modifier
                .background(Color.Transparent)
        ) {
            event?.let { validEvent ->
                EventImage(event = validEvent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = validEvent.eventName,
                        modifier = Modifier.padding(10.dp),
                    )

                    if (showEditIcon) {
                        EditIcon(event = validEvent) { onEditClick(validEvent) }
                    }
                    if (showFavoriteIcon) {
                        FavoriteIcon(event = validEvent) { onFavClick(validEvent) }
                    }
                }
                Text(
                    text = validEvent.eventHostEmail,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
    }
}