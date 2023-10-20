package com.gathering.android.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.CustomActionButton
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.FrgIntroBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : FullScreenBottomSheet() {

    private lateinit var binding: FrgIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isComposeEnabled) {
            setStyle(
                STYLE_NORMAL,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!isComposeEnabled) {
            binding = FrgIntroBinding.inflate(layoutInflater, container, false)
            return binding.root
        } else {
            val composeView = ComposeView(requireContext())
            composeView.setContent {
                GatheringTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        IntroScreenCompose()
                    }
                }
            }
            return composeView
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isComposeEnabled) {
            val list = listOf(
                IntroPageFragment.AppIntro(
                    R.drawable.img1, resources.getString(R.string.fragment_1_text)
                ), IntroPageFragment.AppIntro(
                    R.drawable.img4, resources.getString(R.string.fragment_2_text)
                ), IntroPageFragment.AppIntro(
                    R.drawable.img5, resources.getString(R.string.fragment_3_text)
                )
            )
            val adapter = IntroViewPagerAdapter(this, list)

            binding.viewPager.adapter = adapter
            binding.dotsIndicator.setViewPager2(binding.viewPager)

            binding.btnSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_introFragment_to_signInFragment)
            }

            binding.btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_introFragment_to_signUpScreen)
            }
        }
    }

    @Composable
    @Preview(showBackground = true, device = "id:pixel_4")
    fun IntroScreenPreview() {
        IntroScreenCompose()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun IntroScreenCompose() {

        val introPages = remember {
            listOf(
                IntroPage(
                    R.drawable.img4,
                    "a platform to create events, such as parties, reunions, and gatherings!"
                ),
                IntroPage(
                    R.drawable.img1,
                    "Bringing your family and friends together has never been so easy!"
                ),
                IntroPage(
                    R.drawable.img5, "Invite guests and manage the event details!"
                )
            )
        }
        val pagerState = rememberPagerState(
            initialPage = 0, initialPageOffsetFraction = 0f
        ) {
            introPages.size
        }

        ShowIntroScreen(pagerState, introPages)
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun ShowIntroScreen(
        pagerState: PagerState,
        introPages: List<IntroPage>
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val introPage = introPages[page]
                val imageResource = introPages[page]
                ShowImageAndDescriptions(imageResource, introPage)
            }
            PageIndicator(introPages, pagerState)
            IntroButtons()
        }
    }

    @Composable
    private fun ShowImageAndDescriptions(
        imageResource: IntroPage,
        introPage: IntroPage
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(imageResource.imageResId),
                contentDescription = introPage.description,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = introPage.description,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun IntroButtons() {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomActionButton(
                text = "SIGN IN",
                onClick = { findNavController().navigate(R.id.action_introFragment_to_signInFragment) },
                isLoading = false
            )

            CustomActionButton(
                text = "SIGN UP",
                onClick = { findNavController().navigate(R.id.action_introFragment_to_signUpScreen) },
                isLoading = false
            )
        }
    }
}
