package com.gathering.android.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

interface Destinations {
    val rout: String
    val icon: ImageVector
    val title: String

    object Home : Destinations {
        override val rout = "Home"
        override val icon = Icons.Filled.Home
        override val title = "Home"
    }

    object MyEvent : Destinations {
        override val rout = "My Event"
        override val icon = Icons.Filled.Event
        override val title = "My Event"
    }

    object Profile : Destinations {
        override val rout = "Profile"
        override val icon = Icons.Filled.Person
        override val title = "Profile"
    }
}