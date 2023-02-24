package com.gathering.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gathering.android.databinding.IntroActivityBinding
import com.gathering.android.singleFragment.ViewPagerAdapterSingleFragment

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: IntroActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IntroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //multi:
//        val adapter = ViewPagerAdapter(this)

        //single fragment:
        val list = listOf(
            AppIntro(
                R.drawable.img1,
                resources.getString(R.string.fragment_1_text)
            ),
            AppIntro(
                R.drawable.img2,
                resources.getString(R.string.fragment_2_text)
            ),
            AppIntro(R.drawable.img3, resources.getString(R.string.fragment_3_text))
        )
        val adapter = ViewPagerAdapterSingleFragment(this, list)



        binding.viewPager.adapter = adapter

        // Set up dot indicator
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        // Update dot indicator when page changes
//        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                binding.dotsIndicator.setSelection(position)
//            }
//        })

        // Set up sign in and sign up buttons
        binding.signinBtn.setOnClickListener {
            // Handle sign in button click
        }

        binding.signupBtn.setOnClickListener {
            // Handle sign up button click
        }
    }
}



