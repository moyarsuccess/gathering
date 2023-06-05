package com.gathering.android.event.myevent.addevent.invitation

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.R
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.BottomSheetInvitationBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.gathering.android.event.myevent.addevent.invitation.viewModel.InviteFriendViewModel
import com.gathering.android.event.myevent.addevent.invitation.viewModel.InviteFriendViewState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InviteFriendBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetInvitationBinding

    @Inject
    lateinit var adapter: ContactListAdapter

    @Inject
    lateinit var viewModel: InviteFriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetInvitationBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionX.init(this)
            .permissions(Manifest.permission.READ_CONTACTS)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    init()
                }
            }
    }

    private fun init() {
        binding.etContact.initAutoSuggestion()
        adapter.setOnContactRemoveListener {
            viewModel.onContactRemoveItemClicked(it)

        }

        binding.rvContact.adapter = adapter
        binding.rvContact.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.HORIZONTAL
            )
        )

        binding.btnOk.setOnClickListener {
            val contactList = adapter.getContactItems()
            Log.i("WTF", contactList.toString())
            viewModel.onOKButtonClicked(contactList)
        }

        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        val contacts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("contact_list") as List<Contact>
        } else {
            arguments?.getSerializable("contact_list") as List<Contact>
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                InviteFriendViewState.HideKeyboard -> hideKeyboard()
                InviteFriendViewState.CleaEditText -> binding.etContact.setText("")
                is InviteFriendViewState.SetContact -> binding.etContact.setText(state.contact)
                is InviteFriendViewState.ShowContactList -> showContactAutoSuggestionList(state)
                is InviteFriendViewState.ShowError -> showToast(state.errorMessage)
                is InviteFriendViewState.AddContactToRecyclerView -> adapter.addContactItem(state.contact)

                is InviteFriendViewState.RemoveContactFromRecyclerView -> adapter.deleteContactItem(
                    state.contact
                )

                is InviteFriendViewState.NavigateToAddEvent -> {
                    setNavigationResult(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST, state.contactList)
                    findNavController().popBackStack()
                }
            }
        }

        viewModel.onViewCreated(contacts)
    }

    private fun AutoCompleteTextView.initAutoSuggestion() {
        doOnTextChanged { text, _, _, _ ->
            viewModel.onContactChanged(text.toString())
        }
        onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            viewModel.onSuggestionContactClicked(text.toString())
        }
    }

    private fun showContactAutoSuggestionList(state: InviteFriendViewState.ShowContactList) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_item,
            state.contactList.toTypedArray()
        )
        binding.etContact.setAdapter(adapter)
    }

    private fun hideKeyboard() {
        val inputSystemService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        val imm = inputSystemService as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun showToast(text: String?) {
        Toast.makeText(
            requireContext(), text, Toast.LENGTH_LONG
        ).show()
    }
}