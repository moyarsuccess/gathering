package com.gathering.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.databinding.ScreenProfileBinding
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
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
        binding = ScreenProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun navigateToFavoriteEvent() {
        findNavController()
            .navigate(R.id.action_navigation_profile_to_favoriteEvent)
    }

    override fun navigateToEditProfile() {
        findNavController()
            .navigate(R.id.action_navigation_profile_to_updateUserInfoBottomSheetFragment)
    }

    override fun navigateToIntro() {
        findNavController()
            .navigate(R.id.action_navigation_profile_to_introFragment)
    }
}