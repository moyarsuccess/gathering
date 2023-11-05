package com.gathering.android.profile.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShowUserDetails(displayName: String, email: String) {
    Column(
        modifier = Modifier.padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = displayName,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = email,
            style = MaterialTheme.typography.labelLarge
        )
    }
}