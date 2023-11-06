package com.gathering.android.common.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationBarPaddingSpacer() {
    Spacer(
        modifier = Modifier
            .height(50.dp)
            .navigationBarsPadding()
    )
}