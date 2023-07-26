package com.gathering.android.profile.favoriteEvent

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
class FavoriteEventAdaptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) : RecyclerView.Adapter<FavoriteEventAdaptor.ViewHolder>() {

    private var eventItemList: MutableList<Event> = mutableListOf()
    private var onEventClicked: (event: Event) -> Unit = {}
    private var onFavoriteImageClickListener: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)

    fun setEventItem(eventItemList: List<Event>) {
        this.eventItemList.clear()
        this.eventItemList.addAll(eventItemList)
        notifyItemRangeInserted(0, eventItemList.size)
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
        holder.bind(eventItemList[position])
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
}