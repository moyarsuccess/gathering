package com.gathering.android.event.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.databinding.FrgSortDialogBinding

//TODO: this class does not keep the state of sorted options. it will be fixed in T28
class SortDialogFragment(private val eventListViewModel: EventListViewModel) : DialogFragment() {

    private lateinit var binding: FrgSortDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FrgSortDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        binding.btnSortCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnSortApply.setOnClickListener {
            when (binding.sortRadioGroup.checkedRadioButtonId) {
                R.id.sort_date -> {
                    eventListViewModel.onSortChanged(SortType.SORT_BY_DATE)
                }

                R.id.sort_location -> {
                    eventListViewModel.onSortChanged(SortType.SORT_BY_LOCATION)
                }
            }
            dismiss()
        }
        dialog.dismiss()
        return dialog
    }

    companion object {
        const val TAG = "sort_dialog"
    }
}

enum class SortType {
    SORT_BY_LOCATION, SORT_BY_DATE
}


