package com.gathering.android.event.myevent

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemEventBinding
import com.gathering.android.event.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class MyEventAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<MyEventAdapter.ViewHolder>() {

    private var myEventItemList: MutableList<Event> = mutableListOf()
    private var onFavoriteImageClickListener: (event: Event) -> Unit = {}
    private var onMyEventClicked: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)
    private var onEditClickListener: (event: Event) -> Unit = {}

    fun getEventAtPosition(position: Int): Event? {
        if (position in 0 until myEventItemList.size) {
            return myEventItemList[position]
        }
        return null
    }

    fun setOnEditImageClick(onEditImageClickListener: (event: Event) -> Unit) {
        this.onEditClickListener = onEditImageClickListener
    }

    fun updateEvent(event: Event) {
        val indexOfItem = this.myEventItemList.indexOfFirst { it.eventId == event.eventId }
        this.myEventItemList[indexOfItem] = event
        notifyItemChanged(indexOfItem)
    }

    fun appendEventItems(eventItemList: List<Event>) {
        val startPosition = this.myEventItemList.size
        this.myEventItemList.addAll(eventItemList)
        notifyItemRangeInserted(startPosition, eventItemList.size)
    }

    fun clearData() {
        val size = this.myEventItemList.size
        myEventItemList = mutableListOf()
        notifyItemRangeRemoved(0, size)
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
            onEditClickListener = onEditClickListener,
            onMyEventEventClickListener = onMyEventClicked,
            onFavoriteImageClickListener = onFavoriteImageClickListener
        )
    }

    override fun getItemCount(): Int = myEventItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(myEventItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemEventBinding,
        private val onEditClickListener: (event: Event) -> Unit,
        private val onMyEventEventClickListener: (event: Event) -> Unit,
        private val onFavoriteImageClickListener: (event: Event) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(event: Event) {
            itemBinding.cardView.setOnClickListener {
                onMyEventEventClickListener(event)
            }
            itemBinding.tvEventTitle.text = event.eventName
            itemBinding.tvEventDescription.text = event.description
            imageLoader.loadImage(event.photoUrl, itemBinding.imgEvent)
            itemBinding.tvEventHost.text = event.eventHostEmail

            itemBinding.imgFavorite.setOnClickListener {
                onFavoriteImageClickListener(event)
            }
            val resId = if (event.liked) R.drawable.ic_liked else R.drawable.ic_unliked
            itemBinding.imgFavorite.setImageResource(resId)

            itemBinding.imgEdit.setOnClickListener {
                onEditClickListener(event)
            }
        }
    }
}