package com.gathering.android.event.myevent.rsvpDetails


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
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
class RsvpListScreen : FullScreenBottomSheet() {

    private lateinit var binding: ScreenConfirmedAttendeesBinding


    @Inject
    lateinit var viewModel: RsvpListViewModel

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
                                        showNoData = state.value.showNoData,
                                        imageUrl = imageUrl,
                                        eventName = eventName,
                                        onTabSelected = {},
                                        attendees = state.value.attendees
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
        imageUrl: String,
        eventName: String,
        showNoData: Boolean,
        attendees: List<Attendee>,
        onTabSelected: () -> Unit
    ) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("GOING", "NOT GOING", "MAYBE")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(5.dp))
            CustomTextView("\"$eventName\"")
            Spacer(modifier = Modifier.padding(10.dp))
            EventImageView(imageUrl)
            AcceptTypeTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                    onTabSelected()
                }
            )
            AttendeeList(attendees = attendees.filterByTab(tabs[selectedTabIndex]), showNoData)
        }
    }

    @Composable
    private fun AcceptTypeTabRow(
        tabs: List<String>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit
    ) {
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
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }

    @Composable
    fun AttendeeList(attendees: List<Attendee>, showNoData: Boolean) {
        LazyColumn {
            item {
                if (showNoData) {
                    NoDataText()
                }
            }
            items(attendees) { attendee -> AttendeeItem(attendee = attendee) }
        }
    }

    @Composable
    private fun NoDataText() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextView("hmmm ... No RSVPs yet!")
        }
    }

    private fun List<Attendee>.filterByTab(tab: String): List<Attendee> = when (tab) {
        "GOING" -> filter { it.accepted == "COMING" }
        "NOT GOING" -> filter { it.accepted == "NOT_COMING" }
        "MAYBE" -> filter { it.accepted == "MAYBE" }
        else -> this
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
            showNoData = true,
            onTabSelected = {},
            attendees = emptyList()
        )
    }
}