package com.gathering.android.profile.favoriteEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.toEvent
import javax.inject.Inject

class FavoriteEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<FavoriteEventViewState>()
    val viewState: LiveData<FavoriteEventViewState> by ::_viewState

    fun onViewCreated() {
        eventRepository.getFirstFavoriteEvent { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(FavoriteEventViewState.ShowNoData)
                    _viewState.setValue(FavoriteEventViewState.HideProgress)
                }

                is ResponseState.Success<List<EventModel>> -> {
                    _viewState.setValue(
                        FavoriteEventViewState
                            .ShowFavoriteEvent(request.data.map { it.toEvent() })
                    )
                    _viewState.setValue(FavoriteEventViewState.HideNoData)
                    _viewState.setValue(FavoriteEventViewState.HideProgress)
                }
            }
        }
    }

    fun onLastItemReached() {
        eventRepository.getNextFavoriteEvent { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(FavoriteEventViewState.HideProgress)
                }

                is ResponseState.Success<List<EventModel>> -> {
                    _viewState.setValue(FavoriteEventViewState.HideProgress)
                    _viewState.setValue(FavoriteEventViewState.AppendEventList(request.data.map { it.toEvent() }))
                }
            }
        }
    }

    fun onEventLikeClicked(event: Event) {
        val liked = !event.liked
        val eventId = event.eventId
        eventRepository.likeEvent(eventId, liked) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(
                        FavoriteEventViewState.ShowError(
                            request.throwable.message ?: ""
                        )
                    )
                }

                is ResponseState.Success -> {
                    _viewState.setValue(FavoriteEventViewState.UpdateEvent(event.copy(liked = !event.liked)))
                }
            }
        }
    }

    companion object {
        const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
    }
}