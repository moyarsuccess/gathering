package com.gathering.android.ui.theme

import androidx.annotation.StringRes
import com.gathering.android.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {

    data object Home : Screen("Home", R.string.home)
    data object Profile : Screen("profile", R.string.profile)
    data object MyEvent : Screen("My Event", R.string.my_events)
}