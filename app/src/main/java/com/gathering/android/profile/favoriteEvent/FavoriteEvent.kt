package com.gathering.android.profile.favoriteEvent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.databinding.BottomSheetFavoriteEventBinding

class FavoriteEvent : FullScreenBottomSheet() {

    lateinit var binding: BottomSheetFavoriteEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFavoriteEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }
}