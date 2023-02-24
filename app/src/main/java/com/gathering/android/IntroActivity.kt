package com.gathering.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gathering.android.databinding.IntroActivityBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: IntroActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IntroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = ViewPagerAdapter(Fragment())
        binding.viewPager.adapter = pagerAdapter

        // Attach the DotsIndicator to the ViewPager2
        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }
}

