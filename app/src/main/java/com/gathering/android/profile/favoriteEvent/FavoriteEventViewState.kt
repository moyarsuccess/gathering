package com.gathering.android.profile.favoriteEvent

import com.gathering.android.event.Event

sealed interface FavoriteEventViewState {

    class ShowFavoriteEvent(val eventList: List<Event>) : FavoriteEventViewState

    class AppendEventList(val eventList: List<Event>) : FavoriteEventViewState

    class ShowError(val errorMessage: String) : FavoriteEventViewState

    class UpdateEvent(val event: Event) : FavoriteEventViewState

    object ShowProgress : FavoriteEventViewState

    object HideProgress : FavoriteEventViewState

    object ShowNoData : FavoriteEventViewState

    object HideNoData : FavoriteEventViewState
}