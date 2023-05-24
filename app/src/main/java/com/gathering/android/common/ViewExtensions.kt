package com.gathering.android.common

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.event.myevent.addevent.invitation.model.Contact

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)


fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Fragment.getNavigationResultLiveDataList(key: String = "results") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<Contact>>(key)

fun Fragment.setNavigationResultList(results: List<Contact>, key: String = "results") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, results)
}
