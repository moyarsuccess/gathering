package com.gathering.android

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.databinding.ItemViewPagerBinding

class ViewPagerAdapter( private val list: MutableList<AppIntro>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
            val binding = ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewPagerHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
            val image = labelList[position]
            val color = colorList[position]
            holder.bind(label, color)
        }

        override fun getItemCount(): Int {
            return labelList.size
        }

        class ViewPagerHolder(private var itemHolderBinding: ItemHolderBinding) :
            RecyclerView.ViewHolder(itemHolderBinding.root) {
            fun bind(label: String, color: String) {
                itemHolderBinding.label.text = label
                itemHolderBinding.root.setBackgroundColor(Color.parseColor(color))
            }
        }
    }

}