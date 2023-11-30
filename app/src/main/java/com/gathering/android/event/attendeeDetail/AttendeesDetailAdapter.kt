package com.gathering.android.event.attendeeDetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.databinding.ItemAttendeeEmailBinding
import com.gathering.android.event.model.AttendeeModel
import javax.inject.Inject

class AttendeesDetailAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendeesDetailAdapter.ViewHolder>() {

    private var attendeesList: List<AttendeeModel> = mutableListOf()

    class ViewHolder(val itemBinding: ItemAttendeeEmailBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ItemAttendeeEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            itemBinding = itemBinding,
        )
    }

    override fun getItemCount(): Int = attendeesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.tvEmail.text = attendeesList[position].email
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(attendeeModels: List<AttendeeModel>) {
        this.attendeesList = attendeeModels
        notifyDataSetChanged()
    }
}