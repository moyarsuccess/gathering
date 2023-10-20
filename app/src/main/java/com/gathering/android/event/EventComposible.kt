package com.gathering.android.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.gathering.android.R
import com.gathering.android.common.ProgressBar

@Preview(showBackground = false)
@Composable
fun EventListPreview() {
    EventList(
        listOf(
            Event(
                eventId = 1,
                eventName = "Ani",
                eventHostEmail = "animan@gmail.com",
                description = "party",
                photoUrl = "",
                latitude = 0.0,
                longitude = null
            ), Event(
                eventId = 2,
                eventName = "Mo",
                eventHostEmail = "mo@gmail.com",
                description = "party2",
                photoUrl = "",
                latitude = 0.0,
                longitude = null
            )
        ),
        onEditClicked = {},
        onItemClick = {},
        onFavClick = {},
        isDisplayed = false,
        isNoData = false,
        isMyEvent = false
    )
}

@Composable
fun EventList(
    events: List<Event>,
    onItemClick: (Event) -> Unit,
    onEditClicked: () -> Unit,
    onFavClick: (Event) -> Unit,
    isDisplayed: Boolean,
    isNoData: Boolean,
    isMyEvent: Boolean
) {
    ProgressBar(
        text = "No event yet",
        isDisplayed = isDisplayed,
        isNoData = isNoData
    )
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(events.distinctBy { it.eventId }) { event ->
            EventItem(
                event = event,
                onItemClick = {
                    onItemClick(it)
                },
                onEditClicked = onEditClicked,
                onFavClick = onFavClick,
                isMyEvent = isMyEvent
            )
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    onItemClick: (Event) -> Unit,
    onEditClicked: () -> Unit,
    onFavClick: (Event) -> Unit,
    isMyEvent: Boolean
) {
    Card(
        Modifier
            .padding(10.dp)
            .clickable {
                onItemClick(event)
            })
    {
        Column(Modifier.background(Color.White)) {
            ImageBox(
                event = event,
                onEditClicked = onEditClicked,
                isMyEvent = isMyEvent
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = event.eventName
                )
                FavoriteIcon(event = event, onFavClick)
            }
            Text(
                modifier = Modifier.padding(10.dp),
                text = event.eventHostEmail
            )
        }
    }
}

@Composable
fun FavoriteIcon(event: Event, onFavClick: (Event) -> Unit) {

    IconButton(
        onClick = {
            onFavClick(event)
        }
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

@Composable
fun ImageBox(
    event: Event,
    onEditClicked: () -> Unit,
    isMyEvent: Boolean
) {
    val commentsAlpha = if (isMyEvent) 1f else 0f
    val painter = rememberImagePainter(
        data = "https://moyar.dev:8080/photo/${event.photoUrl}",
        builder = {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(com.google.android.material.R.drawable.mtrl_ic_error)
        }
    )
    Box(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .size(160.dp)
        )
        IconButton(
            onClick = { onEditClicked() },
            modifier = Modifier.alpha(commentsAlpha)
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
                    .size(24.dp)
            )
        }
    }
}