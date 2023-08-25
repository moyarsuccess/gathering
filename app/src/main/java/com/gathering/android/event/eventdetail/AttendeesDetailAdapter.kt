package com.gathering.android.event.eventdetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.databinding.ItemAttendeeEmailBinding
import com.gathering.android.event.model.Attendee
import javax.inject.Inject

class AttendeesDetailAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendeesDetailAdapter.ViewHolder>() {

    private var attendeesList: List<Attendee> = mutableListOf()
    private var filteredAttendees: List<Attendee> = mutableListOf()

    class ViewHolder(val itemBinding: ItemAttendeeEmailBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ItemAttendeeEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            itemBinding = itemBinding,
        )
    }

    override fun getItemCount(): Int = filteredAttendees.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.tvEmail.text = filteredAttendees[position].email
    }

    fun setItems(attendees: List<Attendee>) {
        this.attendeesList = attendees
        setCurrentAcceptType(AcceptType.Yes)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentAcceptType(acceptType: AcceptType) {
        filteredAttendees = attendeesList.filter { it.accepted == acceptType.type }
        notifyDataSetChanged()
    }
}