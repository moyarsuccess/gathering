package com.gathering.android.event.myevent.addevent.invitation.viewModel

import com.gathering.android.event.myevent.addevent.invitation.model.Contact

sealed interface InviteFriendViewState {

    class NavigateToAddEvent(val contactList: List<Contact>) : InviteFriendViewState

    class ShowError(val errorMessage: String?) : InviteFriendViewState

    class ShowContactList(val contactList: List<Contact>) : InviteFriendViewState

    class SetContact(val contact: String) : InviteFriendViewState

    class AddContactToRecyclerView(val contact: Contact) : InviteFriendViewState
    class RemoveContactFromRecyclerView(val contact: Contact) : InviteFriendViewState

    object CleaEditText : InviteFriendViewState

    object HideKeyboard : InviteFriendViewState
}