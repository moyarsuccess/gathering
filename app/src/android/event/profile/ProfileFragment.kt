package com.gathering.android.event.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gathering.android.databinding.FrgProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FrgProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FrgProfileBinding.inflate(layoutInflater)
        return binding.root
    }
}