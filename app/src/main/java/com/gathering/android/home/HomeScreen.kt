package com.gathering.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.rememberImagePainter
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.ProgressBar
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenHomeBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.home.FilterDialogFragment.Companion.TAG
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreen : Fragment(), HomeNavigator {

    private lateinit var binding: ScreenHomeBinding

    @Inject
    lateinit var adapter: HomeEventsAdapter

    @Inject
    lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenHomeBinding.inflate(layoutInflater)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            EventList(
                                state.value.events,
                                viewModel::onEventItemClicked,
                                {},
                                viewModel::onEventLikeClicked,
                                isDisplayed = state.value.showProgress,
                                isNoData = state.value.showNoData
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvEvent.layoutManager = linearLayoutManager
            binding.rvEvent.adapter = adapter

            binding.rvEvent.addOnScrollListener(EndlessScrollListener {
                viewModel.onNextPageRequested()
            })

            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    binding.prg.isVisible = state.showProgress

                    state.errorMessage?.let {
                        showErrorText(it)
                    }
                    binding.tvNoData.isVisible = state.showNoData
                    adapter.updateEvents(state.events)
                }
            }
            adapter.setOnEventClickListener {
                viewModel.onEventItemClicked(it)
            }

            adapter.setOnFavoriteImageClick { event ->
                viewModel.onEventLikeClicked(event)
            }

            binding.sortButton.setOnClickListener {
                val dialog = SortDialogFragment(viewModel)
                dialog.show(parentFragmentManager, TAG)
            }

            binding.filterButton.setOnClickListener {
                val dialog = FilterDialogFragment(viewModel)
                dialog.show(parentFragmentManager, TAG)
            }

            viewModel.onViewCreated(this)
        }
    }

    override fun navigateToEventDetail(event: Event) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT to event)
        findNavController().navigate(
            R.id.action_navigation_home_to_EventDetailScreen, bundle
        )
    }

    override fun navigateToIntroScreen() {
        if (R.id.verificationScreen != findNavController().currentDestination?.id &&
            R.id.newPasswordInputScreen != findNavController().currentDestination?.id
        ) {
            findNavController().navigate(R.id.action_homeScreen_to_introFragment)
        }
    }

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
            isNoData = false
        )
    }

    @Composable
    private fun EventList(
        events: List<Event>,
        onItemClick: (Event) -> Unit,
        onEditClicked: () -> Unit,
        onFavClick: (Event) -> Unit,
        isDisplayed: Boolean,
        isNoData: Boolean
    ) {
        ProgressBar(
            text = "No event yet",
            isDisplayed = isDisplayed,
            isNoData = isNoData
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(events.distinct()) { event ->
                EventItem(
                    event = event,
                    onItemClick = onItemClick,
                    onEditClicked = onEditClicked,
                    onFavClick = onFavClick,
                )
            }
        }
    }

    @Composable
    private fun EventItem(
        event: Event,
        onItemClick: (Event) -> Unit,
        onEditClicked: () -> Unit,
        onFavClick: (Event) -> Unit
    ) {
        Card(
            Modifier
                .padding(10.dp)
                .clickable { onItemClick })
        {
            Column(Modifier.background(White)) {
                ImageBox(event, onEditClicked)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = event.eventName
                    )
                    IconButton(onClick = { onFavClick }) {
                        Icon(
                            Icons.Filled.FavoriteBorder,
                            contentDescription = "", modifier = Modifier
                                .padding(10.dp)
                                .size(32.dp)
                                .background(White)
                        )
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
    fun ImageBox(
        event: Event,
        onEditClicked: () -> Unit
    ) {
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

            IconButton(onClick = { onEditClicked }) {
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
}
