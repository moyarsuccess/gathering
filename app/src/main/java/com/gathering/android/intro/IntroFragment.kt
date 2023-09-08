package com.gathering.android.intro

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : DialogFragment() {

    private lateinit var binding: FrgIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrgIntroBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = listOf(
            IntroPageFragment.AppIntro(
                R.drawable.img1,
                resources.getString(R.string.fragment_1_text)
            ),
            IntroPageFragment.AppIntro(
                R.drawable.img4,
                resources.getString(R.string.fragment_2_text)
            ),
            IntroPageFragment.AppIntro(
                R.drawable.img5,
                resources.getString(R.string.fragment_3_text)
            )
        )
        val adapter = IntroViewPagerAdapter(this, list)

        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signInFragment)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signUpScreen)
        }
    }
}