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

        var appIntro = AppIntro(
            R.drawable.img2,
            "a platform to create events, such as parties, reunions, and gatherings"
        )
        var appIntro2 = AppIntro(
            R.drawable.img1,
            "Bringing you family and friends together has never been so easy"
        )
        var appIntro3 = AppIntro(R.drawable.img3, "invite guests and manage the event details")


        list.add(appIntro)
        list.add(appIntro2)
        list.add(appIntro3)

        // Set adapter to viewPager.
        binding.viewpager.adapter = ViewPagerAdapter(list)
    }

}