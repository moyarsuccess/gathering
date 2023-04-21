package com.gathering.android.event.myevent.addevent.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.databinding.BottomSheetAddLocationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddLocationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddLocationBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }
}