package com.gathering.android.event.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemEventBinding
import com.gathering.android.event.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EventListAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private var eventItemList: MutableList<Event> = mutableListOf()
    private var onEventClicked: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)

    @SuppressLint("NotifyDataSetChanged")
    fun setEventItem(eventItemList: MutableList<Event>) {
        this.eventItemList.clear()
        this.eventItemList.addAll(eventItemList)
        notifyDataSetChanged()
    }

    fun setOnEventClickListener(onEventClicked: (event: Event) -> Unit) {
        this.onEventClicked = onEventClicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEventBinding.inflate(li), onEventClicked)
    }

    override fun getItemCount(): Int = eventItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(eventItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemEventBinding,
        private val onEventClickListener: (event: Event) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(event: Event) {
            itemBinding.cardView.setOnClickListener {
                onEventClickListener(event)
            }
            itemBinding.tvEventTitle.text = event.eventName
            itemBinding.tvEventDescription.text = event.description
            imageLoader.loadImage(event.photoUrl, itemBinding.imgEvent)
        }
    }
}