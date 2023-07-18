package com.gathering.android.event.myevent

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemMyEventBinding
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
    private var onMyEventClicked: (event: Event) -> Unit = {}
    private val li = LayoutInflater.from(context)


    @SuppressLint("NotifyDataSetChanged")
    fun setEventItem(myEventItemList: MutableList<Event>) {
        this.myEventItemList.clear()
        this.myEventItemList.addAll(myEventItemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyEventBinding.inflate(li), onMyEventClicked)

    }

    override fun getItemCount(): Int = myEventItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(myEventItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemMyEventBinding,
        private val onMyEventEventClickListener: (event: Event) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(event: Event) {
            itemBinding.cardView.setOnClickListener {
                onMyEventEventClickListener(event)
            }
            itemBinding.tvEventName.text = event.eventName
            itemBinding.tvAddress.text = event.location.addressLine
            itemBinding.tvEventDescription.text = event.description
            itemBinding.tvEventDateTime.text = event.dateAndTime.toString()
            imageLoader.loadImage(event.photoUrl, itemBinding.imgEvent)
        }
    }
}