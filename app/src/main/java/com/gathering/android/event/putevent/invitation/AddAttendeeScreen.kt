package com.gathering.android.event.putevent.invitation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.composables.AlertTextView
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.composables.CustomTextField
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.ScreenAddAttendeesBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.ui.theme.CustomBackgroundColor
import com.gathering.android.ui.theme.GatheringTheme
import com.gathering.android.ui.theme.Pink80
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddAttendeeScreen : FullScreenBottomSheet(), AddAttendeeNavigator {

    private lateinit var binding: ScreenAddAttendeesBinding

    @Inject
    lateinit var adapter: AttendeeListAdapter

    @Inject
    lateinit var viewModel: AddAttendeesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenAddAttendeesBinding.inflate(LayoutInflater.from(requireContext()))
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            AddAttendeeScreenWithCompose(attendeeEmail = state.value.attendeeEmail,
                                errorMessage = state.value.errorMessage,
                                attendees = state.value.attendeesEmailList,
                                onAttendeeEmailChanged = viewModel::onAttendeeEmailChanged,
                                onAddClick = { viewModel.onAddAttendeeButtonClicked(it) },
                                onRemoveClick = { viewModel.onAttendeeRemoveItemClicked(it) },
                                onSaveClick = { viewModel.onOKButtonClicked() })
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isComposeEnabled) {
            val attendees = arguments?.getString(ATTENDEE_LIST)
            viewModel.onViewCreated(attendees = attendees, addAttendeeNavigator = this)
            return
        } else {
            setRecyclerViewAndInteractions()
        }
    }

    private fun setRecyclerViewAndInteractions() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.updateAttendeeItems(state.attendeesEmailList)

                binding.btnAddAttendee.isEnabled = state.addAttendeeButtonEnable
            }
        }

        adapter.setOnAttendeeRemoveListener {
            viewModel.onAttendeeRemoveItemClicked(it)
        }

        binding.rvContact.adapter = adapter
        binding.rvContact.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.HORIZONTAL
            )
        )

        binding.etAttendee.doOnTextChanged { text, _, _, _ ->
            viewModel.onAttendeeEmailChanged(text.toString())
        }

        binding.btnOk.setOnClickListener {
            viewModel.onOKButtonClicked()
        }

        binding.btnAddAttendee.setOnClickListener {
            viewModel.onAddAttendeeButtonClicked(binding.etAttendee.text.toString())
            hideKeyboard()
        }


        val attendees = arguments?.getString(ATTENDEE_LIST)
        viewModel.onViewCreated(attendees = attendees, addAttendeeNavigator = this)
    }

    private fun hideKeyboard() {
        val inputSystemService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        val imm = inputSystemService as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun navigateToAddEvent(attendeesEmailList: List<String>) {
        setNavigationResult(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST, attendeesEmailList)
        findNavController().popBackStack()
    }

    @Composable
    @Preview(showBackground = true, device = "id:Nexus One")
    fun AddAttendeeScreenWithComposePreview() {
        AddAttendeeScreenWithCompose(
            attendeeEmail = "animansoubi@gamil.com",
            errorMessage = "",
            attendees = listOf(
                "idaoskooei@gmail.com", "esii_pisces@yahoo.com"
            ),
            onAttendeeEmailChanged = {},
            onAddClick = {},
            onSaveClick = {},
            onRemoveClick = {},
        )
    }

    @Composable
    fun AddAttendeeScreenWithCompose(
        attendeeEmail: String?,
        errorMessage: String? = null,
        attendees: List<String>,
        onAttendeeEmailChanged: (String) -> Unit,
        onAddClick: (attendeeEmail: String) -> Unit,
        onRemoveClick: (attendeeEmail: String) -> Unit,
        onSaveClick: () -> Unit,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            AttendeeInputSection(attendeeEmail = attendeeEmail ?: "",
                onAddClick = { attendeeEmail ->
                    onAddClick(attendeeEmail)
                },
                onEmailChange = { onAttendeeEmailChanged(it) })
            AttendeeList(attendees = attendees, onRemoveClick = onRemoveClick)

            if (errorMessage != null) {
                AlertTextView(errorMessage)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            ButtonSave(onSaveClick = { onSaveClick() })
        }
    }

    @Composable
    fun AttendeeInputSection(
        attendeeEmail: String,
        onAddClick: (attendeeEmail: String) -> Unit,
        onEmailChange: (attendeeEmail: String) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            ) {
                CustomTextField(
                    value = attendeeEmail,
                    label = "Add attendee email",
                    onValueChange = { onEmailChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
            IconButtonAdd { onAddClick(attendeeEmail) }
        }
    }

    @Composable
    private fun AttendeeList(
        attendees: List<String>,
        onRemoveClick: (attendeeEmail: String) -> Unit,
    ) {
        Box(modifier = Modifier.padding(bottom = 60.dp)) {
            LazyColumn {
                items(attendees) { attendeeEmail ->
                    AttendeeItem(
                        attendeeEmail = attendeeEmail,
                        onRemoveClick = { onRemoveClick(attendeeEmail) }
                    )
                }
            }
        }
    }

    @Composable
    fun AttendeeItem(
        attendeeEmail: String, onRemoveClick: (attendeeEmail: String) -> Unit
    ) {
        Card(
            modifier = Modifier.padding(7.dp),
            colors = CardDefaults.cardColors(containerColor = CustomBackgroundColor)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp)
            ) {
                Text(
                    text = attendeeEmail,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                IconButtonRemove { onRemoveClick(attendeeEmail) }
            }
        }
    }

    @Composable
    private fun ButtonSave(onSaveClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom
        ) {
            CustomActionButton(
                isLoading = false,
                text = "CLICK TO SAVE",
                onClick = { onSaveClick() },
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )
        }
    }

    @Composable
    private fun IconButtonRemove(onRemoveClick: () -> Unit) {
        IconButton(
            onClick = { onRemoveClick() }, modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircle,
                contentDescription = "Remove",
                tint = Pink80
            )
        }
    }

    @Composable
    private fun IconButtonAdd(onAddClick: () -> Unit) {
        IconButton(
            onClick = { onAddClick() },
            modifier = Modifier.then(Modifier.size(48.dp))
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "", Modifier.size(50.dp)
            )
        }
    }
}