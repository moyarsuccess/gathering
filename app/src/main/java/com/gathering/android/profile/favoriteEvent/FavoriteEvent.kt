package com.gathering.android.profile.favoriteEvent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.databinding.BottomSheetFavoriteEventBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FavoriteEvent : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetFavoriteEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFavoriteEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }
}