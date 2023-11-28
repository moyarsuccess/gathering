package com.gathering.android.event.rsvpDetails


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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.gathering.android.common.composables.HorizontalDivider
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
                            AttendeesScreenWithRsvpDetail(
                                showNoData = state.value.showNoData,
                                imageUrl = state.value.imageUri ?: "",
                                eventName = state.value.eventName ?: "",
                                attendees = state.value.attendees,
                            )
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
    fun AttendeesScreenWithRsvpDetail(
        imageUrl: String,
        eventName: String,
        showNoData: Boolean,
        attendees: List<Attendee>
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextView(title = "\"$eventName\"")
            Spacer(modifier = Modifier.padding(10.dp))
            EventImageView(imageUrl)
            Spacer(modifier = Modifier.padding(20.dp))
            CustomTextView(title = "GUEST LIST")
            Spacer(modifier = Modifier.padding(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(10.dp))
            AttendeeList(
                attendees = attendees, showNoData = showNoData
            )
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
                .fillMaxWidth(),
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
        ) {
            EventImage(imageUrl = imageUrl)
        }
    }

    @Preview(showBackground = true, device = "id:pixel_2")
    @Composable
    fun RsvpScreenPreview() {
        AttendeesScreenWithRsvpDetail(
            imageUrl = "",
            eventName = "hello",
            showNoData = true,
            attendees = listOf(
                Attendee(email = "idaoskooei@gmail.com", accepted = "not coming")
            )
        )
    }
}