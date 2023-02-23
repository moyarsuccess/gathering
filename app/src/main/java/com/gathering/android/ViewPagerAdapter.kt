package com.gathering.android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.databinding.ItemViewPagerBinding

class ViewPagerAdapter(private val list: MutableList<AppIntro>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val binding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val appIntro = list[position]
        holder.bind(appIntro)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewPagerHolder(private var itemViewPagerBinding: ItemViewPagerBinding) :
        RecyclerView.ViewHolder(itemViewPagerBinding.root) {
        fun bind(appIntro: AppIntro) {
            itemViewPagerBinding.imageView1.setImageResource(appIntro.imageId)
            itemViewPagerBinding.description1.text = appIntro.description
        }
    }
}