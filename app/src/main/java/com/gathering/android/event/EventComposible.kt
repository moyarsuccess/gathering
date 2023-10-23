package com.gathering.android.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gathering.android.R
import com.gathering.android.common.NavigationBarPaddingSpacer
import com.gathering.android.common.ProgressBar

@Preview(showBackground = true, device = "id:pixel_2")
@Composable
fun EventListPreview() {
    EventList(
        showFavoriteIcon = false,
        events = listOf(
            Event(
                1, "ani", "animansoubi@gmail.com", "party", "", 0.0, null
            ), Event(
                2, "mo", "animansoubi@gmail.com", "party", "", 0.0, null
            )
        ),
        isLoading = false,
        isNoData = false,
        onFabClick = {},
        showEditIcon = true,
        onEditClick = {},
        onItemClick = {},
        onFavClick = {}
    )
}
@Composable
fun EventList(
    showFavoriteIcon: Boolean = true,
    events: List<Event>,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    isLoading: Boolean,
    isNoData: Boolean,
    onFabClick: () -> Unit,
    showEditIcon: Boolean
) {
    ProgressBar(
        text = "No event yet",
        isLoading = isLoading,
        isNoData = isNoData
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)
            .background(Color.Transparent)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(7.dp)
                .weight(1f)
        ) {
            items(events.distinctBy { it.eventId })
            { event ->
                EventItem(
                    event = event,
                    onItemClick = { onItemClick(event) },
                    onEditClick = { onEditClick(event) },
                    onFavClick = { onFavClick(event) },
                    showFavoriteIcon = showFavoriteIcon,
                    showEditIcon = showEditIcon
                )
                Spacer(modifier = Modifier.padding(15.dp))
            }
        }
        if (!showFavoriteIcon) {
            ShowFabButton(onFabClick = onFabClick)
        } else {
            NavigationBarPaddingSpacer()
        }
    }
}

@Composable
fun ShowFabButton(
    onFabClick: () -> Unit
) {
    Column(Modifier.padding(5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            val customBackgroundColor = Color(0xFFEEEBEB)

            FloatingActionButton(
                onClick = {
                    onFabClick()
                },
                modifier = Modifier
                    .padding(5.dp)
                    .size(56.dp),
                backgroundColor = customBackgroundColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                )
            }
        }
        NavigationBarPaddingSpacer()
    }
}

@Composable
fun EventItem(
    showFavoriteIcon: Boolean,
    event: Event,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    showEditIcon: Boolean,
) {
    Card(
        modifier = Modifier
            .clickable {
                onItemClick(event)
            }, colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ), border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            Modifier
                .background(Color.Transparent)
        ) {
            ShowEventImage(
                event = event
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
                if (showEditIcon) {
                    ShowEditIcon(event = event, onEditClick)
                }
                if (showFavoriteIcon) {
                    ShowFavoriteIcon(event = event, onFavClick)
                }
            }
            Text(
                modifier = Modifier.padding(10.dp),
                text = event.eventHostEmail
            )
        }
    }
}

@Composable
fun ShowEditIcon(event: Event, onEditClick: (Event) -> Unit) {
    IconButton(
        onClick = { onEditClick(event) }
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

@Composable
fun ShowFavoriteIcon(event: Event, onFavClick: (Event) -> Unit) {
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

@Composable
fun ShowEventImage(
    event: Event
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = "https://moyar.dev:8080/photo/${event.photoUrl}")
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(com.google.android.material.R.drawable.mtrl_ic_error)
            }).build()
    )
    Image(
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .size(200.dp)
    )
}