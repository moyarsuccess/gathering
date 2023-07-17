package com.gathering.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.databinding.FrgProfileBinding
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FrgProfileBinding

    @Inject
    lateinit var viewModel: ProfileViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrgProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileViewState.ShowImage -> {
                    val photoUrl = state.imgUrl
                    imageLoader.loadImage(photoUrl, binding.imgProfile)
                }

                ProfileViewState.NavigateToFavoriteEvent -> {
                    findNavController().navigate(R.id.action_navigation_profile_to_favoriteEvent)
                }

                is ProfileViewState.NavigateToPersonalData -> {
                    findNavController().navigate(R.id.action_navigation_profile_to_updateUserInfoBottomSheetFragment)
                }

                is ProfileViewState.SetDisplayName -> {
                    binding.tvDisplayName.text = state.displayName
                }

                is ProfileViewState.SetEmail -> {
                    binding.tvEmail.text = state.email
                }

                ProfileViewState.NavigateToIntro -> {
                    findNavController().navigate(R.id.action_navigation_profile_to_introFragment)
                }
            }
        }

        binding.layoutFavoriteEvent.setOnClickListener {
            viewModel.onFavoriteEventLayoutClicked()
        }

        binding.layoutPersonalData.setOnClickListener {
            viewModel.onPersonalDataLayoutClicked()
        }

        binding.btnSignOut.setOnClickListener {
            viewModel.onSignUpButtonClicked()
        }

        getNavigationResultLiveData<User>(KEY_ARGUMENT_UPDATE_USER)?.observe(viewLifecycleOwner) {
            viewModel.onUserProfileUpdated()
        }

        viewModel.onViewCreated()
    }
}