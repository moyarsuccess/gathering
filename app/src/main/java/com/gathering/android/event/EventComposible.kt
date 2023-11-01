package com.gathering.android.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Snackbar
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gathering.android.R
import com.gathering.android.common.NavigationBarPaddingSpacer
import com.gathering.android.common.ProgressBar
import com.gathering.android.ui.theme.customBackgroundColor

@Preview(showBackground = true, device = "id:pixel_2")
@Composable
fun EventListPreview() {
    EventList(
        showFavoriteIcon = false,
        events = listOf(
            Event(
                1,
                "ani",
                "animansoubi@gmail.com",
                "party",
                "",
                0.0, null
            ), Event(
                2,
                "mo",
                "animansoubi@gmail.com",
                "party",
                "",
                0.0, null
            )
        ),
        isLoading = false,
        isNoData = false,
        onFabClick = {},
        showEditIcon = true,
        onEditClick = {},
        onItemClick = {},
        onFavClick = {},
        onDeleteClick = {},
        onUndoDeleteEvent = {},
        swipeEnabled = true
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventList(
    showFavoriteIcon: Boolean,
    isLoading: Boolean,
    isNoData: Boolean,
    showEditIcon: Boolean,
    swipeEnabled: Boolean,
    events: List<Event>,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    onFabClick: () -> Unit,
    onDeleteClick: (Event) -> Unit,
    onUndoDeleteEvent: (Event) -> Unit,
) {
    var deletedEvent by remember { mutableStateOf<Event?>(null) }

    ProgressBar(
        text = "No event yet",
        isLoading = isLoading,
        isNoData = isNoData
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(7.dp)
                .weight(1f)
        ) {
            items(events.distinctBy { it.eventId }) { event ->
                val state = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        deletedEvent = event
                        onDeleteClick(event)
                        true
                    } else {
                        false
                    }
                })
                if (swipeEnabled) {
                    SwipeableEventItem(
                        state,
                        event,
                        onItemClick,
                        onEditClick,
                        onFavClick,
                        showFavoriteIcon,
                        showEditIcon
                    )
                } else {
                    EventItem(
                        event = event,
                        onItemClick = { onItemClick(event) },
                        onEditClick = { onEditClick(event) },
                        onFavClick = { onFavClick(event) },
                        showFavoriteIcon = showFavoriteIcon,
                        showEditIcon = showEditIcon
                    )
                }

                Spacer(modifier = Modifier.padding(15.dp))
            }
        }

        if (deletedEvent != null) {
            CustomSnackbar(deletedEvent, onUndoDeleteEvent)
        }

        if (!showFavoriteIcon) {
            FabButton(onFabClick = onFabClick)
        } else {
            NavigationBarPaddingSpacer()
        }
    }
}

@Composable
fun EventItem(
    showFavoriteIcon: Boolean,
    event: Event,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    showEditIcon: Boolean
) {
    Card(
        modifier = Modifier
            .clickable {
                onItemClick(event)
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
            EventImage(
                photoUrl = event.photoUrl,
                size = 170.dp, modifier = Modifier
            )
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
                    EditIcon(event = event, onEditClick)
                }
                if (showFavoriteIcon) {
                    FavoriteIcon(event = event, onFavClick)
                }
            }
            Text(
                text = event.eventHostEmail,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SwipeableEventItem(
    state: DismissState,
    event: Event,
    onItemClick: (Event) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    showFavoriteIcon: Boolean,
    showEditIcon: Boolean
) {
    SwipeToDismiss(
        state = state,
        background = {
            val color = when (state.dismissDirection) {
                DismissDirection.StartToEnd -> Color.Transparent
                DismissDirection.EndToStart -> Color.Red
                null -> Color.Transparent
            }
            DeleteIcon(color)

        }, dismissContent = {
            EventItem(
                event = event,
                onItemClick = { onItemClick(event) },
                onEditClick = { onEditClick(event) },
                onFavClick = { onFavClick(event) },
                showFavoriteIcon = showFavoriteIcon,
                showEditIcon = showEditIcon
            )
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}


@Composable
fun EventImage(
    photoUrl: String,
    size: Dp,
    modifier: Modifier
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = photoUrl)
            .apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.img_event)
                error(R.drawable.img_event)
            }).build()
    )
    Card(colors = CardDefaults.cardColors(customBackgroundColor)) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .size(size)
        )
    }
}

@Composable
private fun DeleteIcon(color: Color) {
    Card {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
            )
        }
    }
}

@Composable
fun EditIcon(event: Event, onEditClick: (Event) -> Unit) {
    IconButton(
        onClick = { onEditClick(event) }
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp)
                .size(30.dp)
        )
    }
}

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

@Composable
private fun CustomSnackbar(
    deletedEvent: Event?,
    onUndoDeleteEvent: (event: Event) -> Unit
) {
    var deletedEvent1 by remember { mutableStateOf(deletedEvent) }

    deletedEvent1?.let { event ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            backgroundColor = customBackgroundColor,
            action = {
                TextButton(
                    onClick = {
                        deletedEvent1 = null
                        onUndoDeleteEvent(event)
                    }
                ) {
                    Text(
                        "Undo",
                        modifier = Modifier,
                    )
                }
            }
        ) {
            Text(
                "${event.eventName} deleted",
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun FabButton(
    onFabClick: () -> Unit
) {
    Column(Modifier.padding(5.dp))
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
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