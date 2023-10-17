package com.gathering.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.CustomActionButton
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ScreenProfileBinding
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileScreen : Fragment(), ProfileNavigator {

    private lateinit var binding: ScreenProfileBinding

    @Inject
    lateinit var viewModel: ProfileViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenProfileBinding.inflate(layoutInflater)
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            state.value.displayName?.let {
                                state.value.email?.let { it1 ->
                                    ProfileScreenWithCompose(
                                        it,
                                        it1,
                                        state.value.imageUri
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    imageLoader.loadImage(state.imageUri, binding.imgProfile)
                    binding.tvDisplayName.text = state.displayName
                    binding.tvEmail.text = state.email
                }
            }

            binding.layoutFavoriteEvent.setOnClickListener {
                viewModel.onFavoriteEventLayoutClicked()
            }

            binding.layoutPersonalData.setOnClickListener {
                viewModel.onPersonalDataLayoutClicked()
            }

            binding.btnSignOut.setOnClickListener {
                viewModel.onSignOutButtonClicked()
            }

            getNavigationResultLiveData<User>(KEY_ARGUMENT_UPDATE_USER)?.observe(viewLifecycleOwner) {
                viewModel.onUserProfileUpdated()
            }

            viewModel.onViewCreated(this)
        }


    }

    override fun navigateToFavoriteEvent() {
        findNavController().navigate(R.id.action_navigation_profile_to_favoriteEvent)
    }

    override fun navigateToEditProfile() {
        findNavController().navigate(R.id.action_navigation_profile_to_updateUserInfoBottomSheetFragment)
    }

    override fun navigateToIntro() {
        findNavController().navigate(R.id.action_navigation_profile_to_introFragment)
    }

    @Composable
    @Preview(showBackground = true, device = "id:pixel_7")
    fun ProfileScreenWithComposePreview() {
        ProfileScreenWithCompose("ida", "Idaoskooei@gmail.com", "1234")
    }

    @Composable
    fun ProfileScreenWithCompose(
        displayName: String, email: String, imageUri: String?
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShowProfilePicture(imageUri)

            ShowUserDetails(displayName)

            ShowUserDetails(email)

            ShowFavoriteButton()

            ShowPersonalDataButton()

            CustomActionButton(
                onClick = { viewModel.onSignOutButtonClicked() },
                isLoading = false,
                text = "SIGN OUT"
            )
        }
    }

    @Composable
    private fun ShowPersonalDataButton() {
        Row(
            modifier = Modifier
                .clickable {
                    viewModel.onPersonalDataLayoutClicked()
                }
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "",
                tint = Color.Black
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "PERSONAL DATA",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    private fun ShowProfilePicture(imageUri: String?) {
        Card(
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 20.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Transparent
            )
        ) {
            val painter = if (imageUri.isNullOrEmpty()) {
                painterResource(id = R.drawable.ic_person)
            } else {
                rememberImagePainter(data = imageUri)
            }
            Image(
                painter = painter,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }

    @Composable
    private fun ShowFavoriteButton() {
        Row(
            modifier = Modifier
                .clickable {
                    viewModel.onFavoriteEventLayoutClicked()
                }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "",
                tint = Color.Black
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "FAVORITES",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @Composable
    private fun ShowUserDetails(text: String) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
