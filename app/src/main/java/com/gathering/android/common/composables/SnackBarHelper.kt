package com.gathering.android.common.composables

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val SnackBarHelpers = compositionLocalOf { SnackBarHelper() }

data class SnackBarHelper(
    val scope: CoroutineScope? = null,
    val snackbarHostState: SnackbarHostState? = null
) {
    fun showSnackbar(
        text: String,
        actionText: String,
        onDismissed: () -> Unit,
        onActionPerformed: () -> Unit
    ) {
        scope?.launch {
            val result = snackbarHostState
                ?.showSnackbar(
                    message = text,
                    actionLabel = actionText,
                    duration = SnackbarDuration.Short
                ) ?: return@launch
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    onActionPerformed()
                }

                SnackbarResult.Dismissed -> {
                    onDismissed()
                }
            }
        }
    }
}