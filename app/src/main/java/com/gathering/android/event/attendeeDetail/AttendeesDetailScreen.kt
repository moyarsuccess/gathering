package com.gathering.android.event.attendeeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ScreenAttendeesDetailBinding
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.Attendee
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AttendeesDetailScreen : FullScreenBottomSheet() {

    private lateinit var binding: ScreenAttendeesDetailBinding

    @Inject
    lateinit var adapter: AttendeesDetailAdapter

    @Inject
    lateinit var viewModel: AttendeesDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state =
                                viewModel.uiState.collectAsState(AttendeesDetailViewModel.UiState())
                            AttendeeDetailScreenWithCompose(AttendeesDetailViewModel())
                        }

                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isComposeEnabled) {
            val attendees = arguments?.getSerializable(ATTENDEE_LIST) as List<Attendee>
            viewModel.onViewCreated(attendees)
            return
        } else {
            binding = ScreenAttendeesDetailBinding.inflate(LayoutInflater.from(requireContext()))
            binding.rvAttendees.adapter = adapter
            binding.rvAttendees.layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

            lifecycleScope.launch {
                viewModel.uiState.collectLatest { uiState ->

                    binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                    binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                    binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)

                    when (uiState.selectedAcceptType) {
                        AcceptType.Yes -> {
                            binding.btnYes.setBackgroundResource(R.color.gray)
                        }

                        AcceptType.Maybe -> {
                            binding.btnMaybe.setBackgroundResource(R.color.gray)
                        }

                        AcceptType.No -> {
                            binding.btnNo.setBackgroundResource(R.color.gray)
                        }
                    }
                    adapter.setItems(uiState.selectedAttendeesList)

                    if (uiState.showNoData) {
                        binding.noData.visibility = View.VISIBLE
                    } else {
                        binding.noData.visibility = View.GONE
                    }
                }
            }

            val attendees = arguments?.getSerializable(ATTENDEE_LIST) as List<Attendee>
            viewModel.onViewCreated(attendees)

            binding.btnYes.setOnClickListener {
                viewModel.onAcceptTypeSelectionChanged(AcceptType.Yes)
            }

            binding.btnNo.setOnClickListener {
                viewModel.onAcceptTypeSelectionChanged(AcceptType.No)
            }

            binding.btnMaybe.setOnClickListener {
                viewModel.onAcceptTypeSelectionChanged(AcceptType.Maybe)
            }
        }
    }

    @Composable
    @Preview(showBackground = true, device = "id:Nexus S")
    fun AttendeeDetailScreenPreview() {
        AttendeeDetailScreenWithCompose(AttendeesDetailViewModel())
    }

    @Composable
    fun AttendeeDetailScreenWithCompose(viewModel: AttendeesDetailViewModel) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val acceptTypes = listOf("Yes", "No", "Maybe")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val uiState by viewModel.uiState.collectAsState(initial = AttendeesDetailViewModel.UiState())
            val attendeesList = uiState.selectedAttendeesList

            // Create a TabRow to switch between "Yes," "No," and "Maybe" options
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                acceptTypes.forEachIndexed { index, type ->
                    Tab(
                        text = { Text(text = type) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    if (uiState.showNoData) {
                        Text(
                            text = "No data to show",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                items(attendeesList) { attendeeEmail ->
                    AttendeeListItem(email = attendeeEmail)
                }
            }
        }
    }

    @Composable
    fun AttendeeListItem(email: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp).background(Color.Red),
            elevation = CardDefaults.cardElevation()
        ) {
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}