package com.gathering.android.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.EventsDiffCallback
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemEventBinding
import com.gathering.android.event.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class HomeEventsAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) : RecyclerView.Adapter<HomeEventsAdapter.ViewHolder>() {

    private var eventItemList: MutableList<Event> = mutableListOf()
    private var onEventClicked: (event: Event) -> Unit = {}
    private var onFavoriteImageClickListener: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)
    fun setOnEventClickListener(onEventClicked: (event: Event) -> Unit) {
        this.onEventClicked = onEventClicked
    }

    fun updateEvents(newEventList: List<Event>) {
        val distinctNewEvents = newEventList.distinctBy { it.eventId }
        val diffCallback = EventsDiffCallback(eventItemList, distinctNewEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        eventItemList.clear()
        eventItemList.addAll(distinctNewEvents)

        diffResult.dispatchUpdatesTo(this)
    }


    fun setOnFavoriteImageClick(onFavoriteImageClickListener: (event: Event) -> Unit) {
        this.onFavoriteImageClickListener = onFavoriteImageClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val itemBinding = ItemEventBinding.inflate(li, parent, false)
        return ViewHolder(
            itemBinding = itemBinding,
            onFavoriteImageClickListener = onFavoriteImageClickListener,
            onEventClickListener = onEventClicked
        )
    }

    override fun getItemCount(): Int = eventItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(eventItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemEventBinding,
        private val onEventClickListener: (event: Event) -> Unit,
        private val onFavoriteImageClickListener: (event: Event) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(event: Event) {
            itemBinding.imgEdit.isVisible = false
            itemBinding.tvEventTitle.text = event.eventName
            itemBinding.tvEventDescription.text = event.description
            itemBinding.tvEventHost.text = event.eventHostEmail
            imageLoader.loadImage(event.photoUrl, itemBinding.imgEvent)
            itemBinding.cardView.setOnClickListener {
                onEventClickListener(event)
            }

            itemBinding.imgFavorite.setOnClickListener {
                onFavoriteImageClickListener(event)
            }
            val resId = if (event.liked) R.drawable.ic_liked else R.drawable.ic_unliked
            itemBinding.imgFavorite.setImageResource(resId)
        }
    }
}