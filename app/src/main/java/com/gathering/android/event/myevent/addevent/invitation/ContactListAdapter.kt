package com.gathering.android.event.myevent.addevent.invitation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.ItemContactBinding
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContactListAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    private var contactItemList: MutableList<Contact> = mutableListOf()
    private var onContactRemoveClicked: (contact: Contact) -> Unit = {}
    private val li = LayoutInflater.from(context)

    @SuppressLint("NotifyDataSetChanged")
    fun addContactItem(contact: Contact) {
        this.contactItemList.add(contact)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteContactItem(contact: Contact) {
        this.contactItemList.remove(contact)
        notifyDataSetChanged()
    }

    fun getContactItems(): List<Contact> {
        return contactItemList
    }

    fun setOnContactRemoveListener(onContactRemoveClicked: (contact: Contact) -> Unit) {
        this.onContactRemoveClicked = onContactRemoveClicked
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapter.ViewHolder {
        return ViewHolder(ItemContactBinding.inflate(li), onContactRemoveClicked)
    }

    override fun getItemCount(): Int = contactItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(contactItemList[position])
    }

    inner class ViewHolder(
        private val itemBinding: ItemContactBinding,
        private val onContactRemoveClicked: (contact: Contact) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(contact: Contact) {
            itemBinding.tvContact.text = contact.name
            imageLoader.loadImage(contact.photoUrl, itemBinding.imgContact)
            itemBinding.btnDelete.setOnClickListener {
                onContactRemoveClicked(contact)
            }
        }
    }
}



