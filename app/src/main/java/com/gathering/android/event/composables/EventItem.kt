package com.gathering.android.event.composables

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
    event ?: return
    Card(
        modifier = Modifier.clickable {
            onItemClick(event)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            Modifier.background(Color.Transparent)
        ) {
            EventImage(event = event)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.eventName,
                    modifier = Modifier.padding(10.dp),
                )

                if (showEditIcon) {
                    EditIcon(event = event) { onEditClick(event) }
                }
                if (showFavoriteIcon) {
                    FavoriteIcon(event = event) { onFavClick(event) }
                }
            }
            Text(
                text = event.eventHostEmail,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}