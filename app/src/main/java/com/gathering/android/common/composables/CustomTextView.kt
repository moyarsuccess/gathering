package com.gathering.android.common.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.R

@Composable
@Preview(showBackground = true)
fun CustomTextViewPreview() {
    CustomTextView(textResId = R.string.fragment_1_text)
}

@Composable
fun CustomTextView(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    textStyle: TextStyle = TextStyle.Default
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        val text = stringResource(id = textResId)
        Text(
            text = text,
            style = textStyle,
            modifier = modifier
        )
    }
}