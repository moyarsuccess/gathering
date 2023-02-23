package com.gathering.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gathering.android.databinding.FragmentFirstBinding
import com.gathering.android.databinding.FragmentThirdBinding

class ThirdFragment : Fragment(R.layout.fragment_first) {
    private lateinit var binding: FragmentThirdBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentThirdBinding.bind(view)
        binding.imageView3.setImageResource(R.drawable.img3)
        binding.description3.text = "This is the first fragment"
    }
}
