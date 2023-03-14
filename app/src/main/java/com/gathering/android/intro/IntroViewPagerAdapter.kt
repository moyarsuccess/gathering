package com.gathering.android.intro


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class IntroViewPagerAdapter(
    activity: IntroActivity,
    private val list: List<IntroPageFragment.AppIntro>
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return IntroPageFragment.newInstance(list[position].imageId, list[position].description)
    }
}
