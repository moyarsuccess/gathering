package com.gathering.android.auth.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import com.gathering.android.R
import com.gathering.android.common.CustomTextView
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.BottomSheetForgetPassInfoBinding
import com.gathering.android.ui.theme.GatheringTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordInfo : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetForgetPassInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.CustomBottomSheetDialogTheme
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = BottomSheetForgetPassInfoBinding.inflate(layoutInflater)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ResetPasswordComposeView()
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true, device = "id:pixel_7_pro")
    fun ResetPasswordPreview() {
        ResetPasswordComposeView()
    }

    @Composable
    fun ResetPasswordComposeView() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(25.dp)
        ) {
            CustomTitleText(textResId = R.string.forgot_password)
            CustomTextView(textResId = R.string.reset_pass_info1)
            CustomTitleText(textResId = R.string.reset_pass_info2)
            CustomTextView(textResId = R.string.reset_pass_info3)
            CustomItalicTextView(textResId = R.string.reset_pass_info4)
        }
    }

    @Composable
    fun CustomTitleText(@StringRes textResId: Int) {
        CustomTextView(
            textResId = textResId,
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Black
            )
        )
    }

    @Composable
    fun CustomTextView(@StringRes textResId: Int) {
        CustomTextView(
            textResId = textResId,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }

    @Composable
    fun CustomItalicTextView(@StringRes textResId: Int) {
        CustomTextView(
            textResId = textResId,
            textStyle = TextStyle(
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}