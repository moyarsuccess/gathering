package com.gathering.android.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.common.composables.NavigationBarPaddingSpacer
import com.gathering.android.common.composables.ProgressBar
import com.gathering.android.event.composables.CustomSnackbar
import com.gathering.android.event.composables.EventItem
import com.gathering.android.event.composables.FabButton
import com.gathering.android.event.composables.SwipeableEventItem

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

