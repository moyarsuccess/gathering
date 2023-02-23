package com.gathering.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gathering.android.databinding.IntroActivityBinding


class IntroActivity : AppCompatActivity() {
    private lateinit var binding: IntroActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IntroActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewpager

        setupViewPager2()

    }
    private fun setupViewPager2() {
        val list: MutableList<AppIntro> = ArrayList()
        list.add("img2".toInt(),"a platform to create events, such as parties, reunions, and gatherings")
        list.add("img1".toInt(),"Bringing you family and friends together has never been so easy")
        list.add("img3".toInt(),"invite guests and manage the event details")

        // Set adapter to viewPager.
        binding.viewpager.adapter = ViewPagerAdapter()
    }

}