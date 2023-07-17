package com.gathering.android.profile

sealed interface ProfileViewState {

    class ShowImage(val imgUrl: String) : ProfileViewState

    class SetDisplayName(val displayName: String) : ProfileViewState

    class SetEmail(val email: String) : ProfileViewState

    object NavigateToFavoriteEvent : ProfileViewState

    object NavigateToPersonalData : ProfileViewState

    object NavigateToIntro : ProfileViewState
}