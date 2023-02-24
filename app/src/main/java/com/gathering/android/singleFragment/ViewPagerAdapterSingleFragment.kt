package com.gathering.android.singleFragment


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gathering.android.AppIntro
import com.gathering.android.IntroActivity


class ViewPagerAdapterSingleFragment(activity: IntroActivity, private val list: List<AppIntro>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        val fragment =
            PageFragment.newInstance(list[position].imageId, list[position].description)
        return fragment
    }
}
