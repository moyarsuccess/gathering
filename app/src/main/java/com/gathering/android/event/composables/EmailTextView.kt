package com.gathering.android.event.composables

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gathering.android.event.model.AttendeeModel

@Composable
fun EmailTextView(attendeeModel: AttendeeModel) {
    Text(
        style = TextStyle(
            fontSize = 14.sp
        ),
        text = attendeeModel.email ?: "",
        modifier = Modifier.wrapContentWidth(),
        fontWeight = FontWeight.Bold,
    )
}