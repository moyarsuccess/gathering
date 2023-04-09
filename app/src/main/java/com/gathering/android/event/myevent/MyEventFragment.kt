package com.gathering.android.event.myevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gathering.android.databinding.FrgMyEventBinding

class MyEventFragment : Fragment() {

    private lateinit var binding:FrgMyEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FrgMyEventBinding.inflate(layoutInflater)
        return binding.root
    }
}