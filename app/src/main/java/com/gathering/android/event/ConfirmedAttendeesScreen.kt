package com.gathering.android.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ScreenConfirmedAttendeesBinding
import com.gathering.android.ui.theme.GatheringTheme

class ConfirmedAttendeesScreen : FullScreenBottomSheet() {

    private lateinit var binding: ScreenConfirmedAttendeesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenConfirmedAttendeesBinding.inflate(inflater, container, false)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ConfirmedAttendeesComposeView()
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun ConfirmedAttendeesComposeView() {
        Column(modifier = Modifier.fillMaxSize()) {
        }
    }

    @Preview(showBackground = true, device = "id:pixel_4")
    @Composable
    fun ConfirmedAttendeesPreview() {
        ConfirmedAttendeesComposeView()
    }
}
