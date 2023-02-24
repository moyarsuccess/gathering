package com.gathering.android

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.gathering.android.databinding.FragmentFirstBinding
import com.gathering.android.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {

    private lateinit var binding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView3.setImageResource(R.drawable.img3)
        binding.description3.text = getString(R.string.fragment_3_text)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}