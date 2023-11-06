package com.gathering.android.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gathering.android.R

@Composable
fun LogoImage() {
    Row(modifier = Modifier.padding(top = 30.dp, bottom = 100.dp)) {
        val painter =
            painterResource(id = R.drawable.gatherz_high_resolution_logo_transparent)
        Image(painter = painter, contentDescription = "", Modifier.size(200.dp))
    }
}