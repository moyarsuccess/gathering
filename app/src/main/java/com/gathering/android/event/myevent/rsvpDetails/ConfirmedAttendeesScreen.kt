package com.gathering.android.event.myevent.rsvpDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ScreenConfirmedAttendeesBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.composables.AttendeeItem
import com.gathering.android.event.composables.EventImage
import com.gathering.android.event.model.Attendee
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmedAttendeesScreen : FullScreenBottomSheet() {

    private lateinit var binding: ScreenConfirmedAttendeesBinding


    @Inject
    lateinit var viewModel: ConfirmedAttendeeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenConfirmedAttendeesBinding.inflate(inflater, container, false)
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

                            state.value.imageUri?.let { imageUrl ->
                                state.value.eventName?.let { eventName ->
                                    ConfirmedAttendeesComposeView(
                                        imageUrl = imageUrl,
                                        goingAttendees = viewModel.getGoingAttendees(),
                                        notGoingAttendees = viewModel.getNotGoingAttendees(),
                                        maybeGoingAttendees = viewModel.getMaybeAttendees(),
                                        eventName = eventName,
                                        showNoData = state.value.showNoData
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT, Event::class.java)
        } else {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT) as Event
        }
        viewModel.onViewCreated(event = event)
    }


    @Composable
    fun ConfirmedAttendeesComposeView(
        goingAttendees: List<Attendee>,
        notGoingAttendees: List<Attendee>,
        maybeGoingAttendees: List<Attendee>,
        imageUrl: String,
        eventName: String,
        showNoData: Boolean
    ) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.padding(5.dp))

            CustomTextView("\"$eventName\"")

            Spacer(modifier = Modifier.padding(10.dp))

            EventImageView(imageUrl)

            AcceptTypeTabRow(selectedTabIndex = selectedTabIndex) { index ->
                selectedTabIndex = index
            }
            TabContent(
                notGoingAttendees = notGoingAttendees,
                maybeGoingAttendees = maybeGoingAttendees,
                goingAttendees = goingAttendees,
                selectedTabIndex = selectedTabIndex,
                showNoData = showNoData
            )
        }
    }

    @Composable
    private fun AcceptTypeTabRow(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
        val tabs = listOf("GOING", "NOT GOING", "MAYBE")

        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { CustomTextView(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        onTabSelected(index)
                    }
                )
            }
        }
    }

    @Composable
    fun TabContent(
        showNoData: Boolean,
        goingAttendees: List<Attendee>,
        notGoingAttendees: List<Attendee>,
        maybeGoingAttendees: List<Attendee>,
        selectedTabIndex: Int
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                if (showNoData) {
                    ShowNoDataText()
                } else {
                    when (selectedTabIndex) {
                        0 -> ShowAttendees(goingAttendees)
                        1 -> ShowAttendees(notGoingAttendees)
                        2 -> ShowAttendees(maybeGoingAttendees)
                        else -> ShowNoDataText()
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowAttendees(attendees: List<Attendee>) {
        LazyColumn {
            items(attendees) { attendee ->
                AttendeeItem(attendee = attendee)
            }
        }
    }

    @Composable
    private fun ShowNoDataText() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextView("hmmm ... No RSVPs yet!")
        }
    }


    @Composable
    private fun CustomTextView(title: String) {
        Text(
            text = title, style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
            )
        )
    }

    @Composable
    private fun EventImageView(imageUrl: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(250.dp)
                .padding(10.dp)
        ) {
            EventImage(imageUrl = imageUrl)
        }
    }

    @Preview(showBackground = true, device = "id:pixel_4")
    @Composable
    fun ConfirmedAttendeesPreview() {
        ConfirmedAttendeesComposeView(
            imageUrl = "",
            eventName = "hello",
            goingAttendees = listOf(Attendee(email = "idaoskooei@yahoo.com")),
            maybeGoingAttendees = emptyList(),
            notGoingAttendees = emptyList(),
            showNoData = false
        )
    }
}