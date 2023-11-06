package com.gathering.android.event.composables

import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gathering.android.event.Event

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeableEventItem(
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