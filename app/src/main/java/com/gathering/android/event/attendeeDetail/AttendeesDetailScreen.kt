package com.gathering.android.event.attendeeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ScreenAttendeesDetailBinding
import com.gathering.android.event.composables.AttendeeItem
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AttendeesDetailScreen : DialogFragment() {

    private lateinit var binding: ScreenAttendeesDetailBinding

    @Inject
    lateinit var adapter: AttendeesDetailAdapter

    @Inject
    lateinit var viewModel: AttendeesDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenAttendeesDetailBinding.inflate(inflater, container, false)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state =
                                viewModel.uiState.collectAsState(AttendeesDetailViewModel.UiState())

                            AttendeeDetailScreenWithCompose(
                                attendeeModels = state.value.selectedAttendeesList,
                                showNoData = state.value.showNoData,
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
            val attendeeModels = arguments?.getSerializable(ATTENDEE_LIST) as List<AttendeeModel>
            viewModel.onViewCreated(attendeeModels)
            return
        } else {
            recyclerviewAndInteractions()
        }
    }

    private fun recyclerviewAndInteractions() {
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

        val attendeeModels = arguments?.getSerializable(ATTENDEE_LIST) as List<AttendeeModel>
        viewModel.onViewCreated(attendeeModels)

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

    @Composable
    @Preview(showBackground = true, device = "id:Nexus S")
    fun AttendeeDetailScreenPreview() {
        AttendeeDetailScreenWithCompose(attendeeModels = listOf(), showNoData = false)
    }

    @Composable
    fun AttendeeDetailScreenWithCompose(
        attendeeModels: List<AttendeeModel>,
        showNoData: Boolean,
    ) {
        Column(modifier = Modifier)
        {
            Tab(
                modifier = Modifier.background(Color.DarkGray),
                text = { Text(text = "LIST OF EVENT ATTENDEES", color = Color.White) },
                selected = true,
                onClick = {},
                selectedContentColor = Color.LightGray
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                item {
                    if (showNoData) {
                        NoDataMessage()
                    }
                }

                items(attendeeModels) { attendee ->
                    AttendeeItem(attendeeModel = attendee)
                }
            }
        }
    }

    @Composable
    fun NoDataMessage() {
        Text(
            text = "oh,oh! No RSVP Yet.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}