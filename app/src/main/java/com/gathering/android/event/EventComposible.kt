package com.gathering.android.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.common.composables.NavigationBarPaddingSpacer
import com.gathering.android.common.composables.ProgressBar
import com.gathering.android.event.composables.CustomSnackbar
import com.gathering.android.event.composables.EventItem
import com.gathering.android.event.composables.FabButton

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventList(
    showFavoriteIcon: Boolean,
    isLoading: Boolean,
    isNoData: Boolean,
    showEditIcon: Boolean,
    swipeEnabled: Boolean,
    events: List<Event>,
    onItemClick: (eventId: Long) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    onFabClick: () -> Unit,
    onDeleteClick: (Event) -> Unit,
    onUndoDeleteEvent: (Event) -> Unit,
    onNextPageRequested: () -> Unit
) {
    var deletedEvent by remember { mutableStateOf<Event?>(null) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                onNextPageRequested()
                return Offset.Zero
            }
        }
    }

    if (showFavoriteIcon) {
        ProgressBar(
            text = "oooops! No events yet.",
            isLoading = isLoading,
            isNoData = isNoData
        )
    } else {
        ProgressBar(
            text = "You don't have any events yet! click on + button to add your event!",
            isLoading = isLoading,
            isNoData = isNoData
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
                .weight(1f)
                .nestedScroll(nestedScrollConnection)
        ) {
            itemsIndexed(items = events.distinctBy { it.eventId }, key = { _, listItem ->
                listItem.hashCode()
            }) { _, event ->
                val state = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onDeleteClick(event)
                        deletedEvent = event
                    }
                    true
                })
                if (swipeEnabled) {
                    SwipeToDismiss(state = state,
                        background = {
                            val color = when (state.dismissDirection) {
                                DismissDirection.EndToStart -> Color.Red
                                DismissDirection.StartToEnd -> Color.Transparent
                                null -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.align(
                                        Alignment.CenterEnd
                                    )
                                )
                            }
                        }, dismissContent = {
                            EventItem(
                                event = event,
                                onItemClick = { onItemClick(event.eventId) },
                                onEditClick = { onEditClick(event) },
                                onFavClick = { onFavClick(event) },
                                showFavoriteIcon = showFavoriteIcon,
                                showEditIcon = showEditIcon
                            )
                        })
                } else {
                    EventItem(
                        event = event,
                        onItemClick = { onItemClick(event.eventId) },
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
        isShowSnackBar = false,
        showEditIcon = true,
        swipeEnabled = true,
        onEditClick = {},
        onItemClick = {},
        onFabClick = {},
        onFavClick = {},
        onSwipedToDelete = {},
        onUndoClicked = {},
        onSnackBarDismissed = {},
        onNextPageRequested = {},
    )
}