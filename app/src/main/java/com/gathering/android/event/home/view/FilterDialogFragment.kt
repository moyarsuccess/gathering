package com.gathering.android.event.home.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.gathering.android.databinding.FrgFilterDialogBinding
import com.gathering.android.event.home.EventListViewModel

//TODO: this class does not keep the state of filtered options. it will be fixed in T28
class FilterDialogFragment(private val eventListViewModel: EventListViewModel) : DialogFragment() {

    private lateinit var binding: FrgFilterDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FrgFilterDialogBinding.inflate(layoutInflater)

        val builder =
            AlertDialog.Builder(requireContext(), com.gathering.android.R.style.MyAlertDialogStyle)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        binding.btnFilterCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnFilterApply.setOnClickListener {
            eventListViewModel.onFilterChanged(
                Filter(
                    binding.myContactEvents.isChecked,
                    binding.myEvents.isChecked,
                    binding.todayEvents.isChecked
                )
            )
            dialog.dismiss()
        }
        return dialog
    }

     companion object { const val TAG = "filter_dialog"
    }
}

data class Filter(
    val isContactsFilterOn: Boolean = false,
    val isMyEventsFilterOn: Boolean = false,
    val isTodayFilterOn: Boolean = false
)
