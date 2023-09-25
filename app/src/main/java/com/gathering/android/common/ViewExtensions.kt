package com.gathering.android.common

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun <T> Fragment.getNavigationResultLiveData(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)


fun <T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Fragment.showErrorText(text: String) {
    Log.i("WTF", "$this")
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}
