package com.gathering.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.CustomActionButton
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.NavigationBarPaddingSpacer
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

                            ProfileScreenWithCompose(
                                email = state.value.email ?: "",
                                displayName = state.value.displayName ?: "",
                                imageUri = state.value.imageUri,
                                onFavoriteEventLayoutClicked = {
                                    viewModel.onFavoriteEventLayoutClicked()
                                },
                                onPersonalDataLayoutClicked = {
                                    viewModel.onPersonalDataLayoutClicked()
                                },
                                onSignOutButtonClicked = {
                                    viewModel.onSignOutButtonClicked()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigationResultLiveData<User>(KEY_ARGUMENT_UPDATE_USER)?.observe(viewLifecycleOwner) {
            viewModel.onUserProfileUpdated()
        }

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
        ProfileScreenWithCompose(
            "ida",
            "Idaoskooei@gmail.com",
            "1234",
            onFavoriteEventLayoutClicked = {},
            onPersonalDataLayoutClicked = {},
            onSignOutButtonClicked = {}
        )
    }

    @Composable
    fun ProfileScreenWithCompose(
        displayName: String,
        email: String,
        imageUri: String?,
        onFavoriteEventLayoutClicked: () -> Unit,
        onPersonalDataLayoutClicked: () -> Unit,
        onSignOutButtonClicked: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ShowProfilePicture(imageUri) {}

            ShowUserDetails(displayName, email)

            HorizontalDivider()

            ShowProfileContents(onFavoriteEventLayoutClicked, onPersonalDataLayoutClicked)

            CustomActionButton(
                onClick = { onSignOutButtonClicked() },
                isLoading = false,
                text = "SIGN OUT"
            )
            NavigationBarPaddingSpacer()
        }
    }

    @Composable
    private fun ShowProfileContents(
        onFavoriteEventLayoutClicked: () -> Unit,
        onPersonalDataLayoutClicked: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 30.dp)
        ) {
            ProfileIconButtonWithText(
                icon = Icons.Filled.Favorite,
                text = "MY FAVORITE EVENTS"
            ) {
                onFavoriteEventLayoutClicked()
            }
            ProfileIconButtonWithText(
                icon = Icons.Filled.Person2,
                text = "EDIT MY PROFILE"
            ) {
                onPersonalDataLayoutClicked()
            }

            // TODO() TICKET NUMBER 74
            ProfileIconButtonWithText(
                icon = Icons.Filled.NotificationsActive,
                text = "NOTIFICATIONS"
            ) {
            }
        }
    }
}