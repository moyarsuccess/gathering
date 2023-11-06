package com.gathering.android.profile.favoriteEvent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.composables.ProgressBar
import com.gathering.android.databinding.ScreenFavoriteEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.composables.EventItem
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteEventScreen : FullScreenBottomSheet(), FavoriteEventNavigator {

    lateinit var binding: ScreenFavoriteEventBinding

    @Inject
    lateinit var viewModel: FavoriteEventScreenViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GatheringTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val state = viewModel.uiState.collectAsState()
                        FavoriteEventScreenWithCompose(
                            likedEvents = state.value.favoriteEvents,
                            onItemClick = { viewModel.onEventItemClicked(it) },
                            isLoading = state.value.showProgress,
                            isNoData = state.value.showNoData
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        return viewModel.onViewCreated(this)

    }

    override fun navigateToEventDetail(event: Event) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT to event)
        findNavController().navigate(
            resId = R.id.action_navigation_favoriteEventScreen_to_EventDetailScreen,
            args = bundle,
            navOptions = null,
            navigatorExtras = null
        )
    }

    @Composable
    @Preview(showBackground = true, device = "id:pixel_7")
    fun FavoriteEventScreenPreview() {
        FavoriteEventScreenWithCompose(
            onItemClick = {},
            likedEvents = listOf(
                Event(
                    1,
                    "ida",
                    "ida",
                    "party",
                    "", 0.0, 0.0
                )
            ),
            isLoading = false,
            isNoData = false
        )
    }

    @Composable
    fun FavoriteEventScreenWithCompose(
        likedEvents: List<Event>,
        onItemClick: (Event) -> Unit,
        isLoading: Boolean,
        isNoData: Boolean
    ) {
        ProgressBar(
            text = "oops. no Favorite events yet!!",
            isLoading = isLoading,
            isNoData = isNoData
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)

        ) {
            Text(
                text = "My Favorite Events",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = (20.sp))
            )

            Spacer(modifier = Modifier.padding(20.dp))

            FavoriteEventList(
                likedEvents = likedEvents,
                onItemClick = onItemClick
            )
        }
    }

    @Composable
    fun FavoriteEventList(
        likedEvents: List<Event>,
        onItemClick: (Event) -> Unit
    ) {
        LazyColumn {
            items(likedEvents) { event ->
                FavoriteEventItem(
                    onItemClick = onItemClick,
                    event = event
                )
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }

    @Composable
    fun FavoriteEventItem(
        onItemClick: (Event) -> Unit,
        event: Event
    ) {
        EventItem(
            showFavoriteIcon = false,
            onItemClick = onItemClick,
            onEditClick = {},
            onFavClick = {},
            showEditIcon = false,
            event = event
        )
    }
}