package com.gathering.android.event.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemEventBinding
import com.gathering.android.event.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class EventListAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private var eventItemList: MutableList<Event> = mutableListOf()
    private var onEventClicked: (event: Event) -> Unit = {}
    private var onFavoriteImageClickListener: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)

    @SuppressLint("NotifyDataSetChanged")
    fun setEventItem(eventItemList: List<Event>) {
        this.eventItemList.clear()
        this.eventItemList.addAll(eventItemList)
        notifyDataSetChanged()
    }

    fun setEventItem2(eventItemList: List<Event>) {
        val diffCallback = EventDiffCallback(eventItemList, this.eventItemList)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        this.eventItemList.clear()
        this.eventItemList.addAll(eventItemList)
        diffCourses.dispatchUpdatesTo(this)
    }

    fun appendEventItems(eventItemList: List<Event>) {
        val startPosition = this.eventItemList.size
        this.eventItemList.addAll(eventItemList)
        notifyItemRangeInserted(startPosition, this.eventItemList.size)
    }

    fun setOnEventClickListener(onEventClicked: (event: Event) -> Unit) {
        this.onEventClicked = onEventClicked
    }

    fun updateEvent(event: Event) {
        val indexOfItem = this.eventItemList.indexOfFirst { it.eventId == event.eventId }
        this.eventItemList[indexOfItem] = event
        notifyItemChanged(indexOfItem)
    }

    fun setOnFavoriteImageClick(onFavoriteImageClickListener: (event: Event) -> Unit) {
        this.onFavoriteImageClickListener = onFavoriteImageClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventBinding.inflate(li),
            onEventClicked,
            onFavoriteImageClickListener
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


    class EventDiffCallback(private val oldList: List<Event>, private val newList: List<Event>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].eventId == newList[newItemPosition].eventId
        }

        override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {
            val old = oldList[oldCourse]
            val new = newList[newPosition]
            return old.isContentTheSame(new)
        }

        private fun Event.isContentTheSame(other: Event): Boolean {
            if (other.eventId != eventId) return false
            if (other.eventName != eventName) return false
            if (other.eventHostEmail != eventHostEmail) return false
            if (other.description != description) return false
            if (other.photoUrl != photoUrl) return false
            if (other.location != location) return false
            if (other.dateAndTime != dateAndTime) return false
            if (other.isContactEvent != isContactEvent) return false
            if (other.isMyEvent != isMyEvent) return false
            if (other.eventCost != eventCost) return false
            if (other.liked != liked) return false
            if (other.attendees.size != attendees.size) return false
            return true
        }
    }
}