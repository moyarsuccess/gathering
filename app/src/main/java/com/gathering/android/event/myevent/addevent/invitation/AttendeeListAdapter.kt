package com.gathering.android.event.myevent.addevent.invitation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.databinding.ItemAttendeeBinding
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AttendeeListAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : RecyclerView.Adapter<AttendeeListAdapter.ViewHolder>() {

    private var attendeeItemList: MutableList<String> = mutableListOf()
    private var onAttendeeRemoveClicked: (attendee: String) -> Unit = {}
    private val li = LayoutInflater.from(context)

    fun addAttendeeItem(attendee: String) {
        this.attendeeItemList.add(attendee)
        notifyItemInserted(this.attendeeItemList.lastIndex)
    }

    fun deleteAttendeeItem(attendee: String) {
        this.attendeeItemList.remove(attendee)
        notifyItemRemoved(this.attendeeItemList.size)
    }

    fun getAttendeeItems(): List<String> {
        return attendeeItemList
    }

    fun setOnAttendeeRemoveListener(onAttendeeRemoveClicked: (attendee: String) -> Unit) {
        this.onAttendeeRemoveClicked = onAttendeeRemoveClicked
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendeeListAdapter.ViewHolder {
        return ViewHolder(ItemAttendeeBinding.inflate(li), onAttendeeRemoveClicked)
    }

    override fun getItemCount(): Int = attendeeItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(attendeeItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemAttendeeBinding,
        private val onAttendeeRemoveClicked: (attendee: String) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(attendee: String) {
            itemBinding.tvAttendee.text = attendee
            itemBinding.btnDelete.setOnClickListener {
                onAttendeeRemoveClicked(attendee)
            }
        }
    }
}



