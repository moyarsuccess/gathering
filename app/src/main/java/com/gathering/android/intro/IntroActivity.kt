package com.gathering.android.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gathering.android.R
import com.gathering.android.databinding.IntroActivityBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: IntroActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IntroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val list = listOf(
            IntroPageFragment.AppIntro(
                R.drawable.img1,
                resources.getString(R.string.fragment_1_text)
            ),
            IntroPageFragment.AppIntro(
                R.drawable.img2,
                resources.getString(R.string.fragment_2_text)
            ),
            IntroPageFragment.AppIntro(
                R.drawable.img3,
                resources.getString(R.string.fragment_3_text)
            )
        )
        val adapter = IntroViewPagerAdapter(this, list)


        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)


        binding.signinBtn.setOnClickListener {
        }

        binding.signupBtn.setOnClickListener {
        }
    }
}



