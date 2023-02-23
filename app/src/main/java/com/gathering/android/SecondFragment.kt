package com.gathering.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gathering.android.databinding.FragmentFirstBinding
import com.gathering.android.databinding.FragmentSecondBinding

class SecondFragment : Fragment(R.layout.fragment_first) {
    private lateinit var binding: FragmentSecondBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSecondBinding.bind(view)
        binding.imageView2.setImageResource(R.drawable.img2)
        binding.description2.text = "This is the second fragment"
    }
}
