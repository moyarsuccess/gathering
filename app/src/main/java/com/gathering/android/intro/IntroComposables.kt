package com.gathering.android.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gathering.android.common.PageIndicatorView


@Composable
@Preview
fun pageIndicatorPreview() {
    PageIndicatorView(
        isSelected = true,
        selectedColor = Color.Red,
        defaultColor = Color.Red,
        defaultRadius = 5.dp,
        selectedLength = 5.dp,
        animationDurationInMillis = 1
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIndicator(
    introPages: List<IntroPage>,
    pagerState: PagerState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        introPages.forEachIndexed { index, _ ->
            val isSelected = index == pagerState.currentPage
            PageIndicatorView(
                isSelected = isSelected,
                selectedColor = Color.Black,
                defaultColor = Color.Gray,
                defaultRadius = 15.dp,
                selectedLength = 15.dp,
                animationDurationInMillis = 300,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}