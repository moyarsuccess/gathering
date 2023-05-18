package com.gathering.android.event.myevent.addevent.pic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.databinding.BottomSheetAddPicBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddPicBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddPicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddPicBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }
}