package com.gathering.android.common

import android.content.Context
import com.gathering.android.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getEditButtonText(): String {
        return context.getString(R.string.edit_profile)
    }

    fun getSaveButtonText(): String {
        return context.getString(R.string.save_changes)
    }
}