package com.gathering.android.event.myevent.addevent.invitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.databinding.BottomSheetInvitationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InviteFriendBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetInvitationBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }
}