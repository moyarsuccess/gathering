package com.gathering.android.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.common.composables.ProgressNoDataWidget
import com.gathering.android.common.composables.SnackBarHelpers
import com.gathering.android.event.composables.EventItem
import com.gathering.android.event.composables.FabButton

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventList(
    showFavoriteIcon: Boolean,
    showAddFab: Boolean = false,
    deletedEventName: String = "",
    noDataText: String,
    showProgress: Boolean,
    showNoData: Boolean,
    showSnackBar: Boolean,
    showEditIcon: Boolean,
    swipeEnabled: Boolean,
    events: List<Event>,
    onItemClick: (eventId: Long) -> Unit,
    onEditClick: (Event) -> Unit,
    onFavClick: (Event) -> Unit,
    onFabClick: () -> Unit,
    onUndoClicked: () -> Unit,
    onSwipedToDelete: (Event) -> Unit,
    onSnackBarDismissed: () -> Unit,
    onNextPageRequested: () -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                onNextPageRequested()
                return Offset.Zero
            }
        }
    }

    ProgressNoDataWidget(
        noDataText = noDataText,
        showProgress = showProgress,
        showNoData = showNoData
    )

    if (showSnackBar) {
        SnackBarHelpers.current.showSnackbar(
            text = "$deletedEventName deleted.",
            actionText = "Undo",
            onDismissed = { onSnackBarDismissed() },
            onActionPerformed = { onUndoClicked() }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
                .nestedScroll(nestedScrollConnection)
        ) {
            itemsIndexed(
                items = events.distinctBy { it.eventId },
                key = { _, listItem -> listItem.hashCode() }
            ) { _, event ->
                val state = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToStart) {
                            onSwipedToDelete(event)
                        }
                        true
                    }
                )
                if (swipeEnabled) {
                    SwipeToDismiss(
                        state = state,
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
                                    .padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Gray,
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
                        }, directions = setOf(DismissDirection.EndToStart)
                    )
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
        if (showAddFab) FabButton(onFabClick = onFabClick)
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
        noDataText = "No data",
        showProgress = false,
        showNoData = false,
        showSnackBar = false,
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