package com.gathering.android.event.myevent.addevent.invitation.viewModel

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.gathering.android.event.myevent.addevent.invitation.model.ContactRepository
import javax.inject.Inject

class InviteFriendViewModel @Inject constructor(private val contactRepo: ContactRepository) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<InviteFriendViewState>()
    val viewState: ActiveMutableLiveData<InviteFriendViewState> by ::_viewState

    private var contactList = mutableListOf<Contact>()

    fun onContactChanged(contactName: String) {
        val contactList = contactRepo.getContactList(contactName)
        _viewState.setValue(InviteFriendViewState.ShowContactList(contactList))
    }

    fun onOKButtonClicked(contactList: List<Contact>) {
        _viewState.setValue(InviteFriendViewState.NavigateToAddEvent(contactList))
    }

    fun onSuggestionContactClicked(contactName: String) {
        contactRepo.getContactInfo(contactName)?.also { contact ->
            if (contactList.contains(contact)) return
            contactList.add(contact)
            _viewState.setValue(InviteFriendViewState.AddContactToRecyclerView(contact))
        }
        _viewState.setValue(InviteFriendViewState.CleaEditText)
        _viewState.setValue(InviteFriendViewState.HideKeyboard)
    }

    fun onContactRemoveItemClicked(contact: Contact) {
        contactList.remove(contact)
        _viewState.setValue(InviteFriendViewState.RemoveContactFromRecyclerView(contact))
    }

    fun onViewCreated(contacts: List<Contact>) {
        contactList = contacts as MutableList<Contact>
        contacts.forEach { item ->
            _viewState.setValue(InviteFriendViewState.AddContactToRecyclerView(item))
        }
    }
}