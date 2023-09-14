package com.gathering.android.profile.editProfile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenEditProfileBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileScreen : FullScreenBottomSheet(), EditeProfileNavigator {

    lateinit var binding: ScreenEditProfileBinding

    @Inject
    lateinit var viewModel: EditeProfileViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenEditProfileBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                imageLoader.loadImage(state.imageUri, binding.imgProfile)
                binding.tvEmail.text = state.email
                binding.etDisplayName.setText(state.displayName)
                binding.btnSave.isEnabled = state.saveButtonEnable!!
                if (!state.errorMessage.isNullOrEmpty()) {
                    showErrorText(state.errorMessage)
                }
            }
        }

        binding.imgProfile.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.btnSave.setOnClickListener {
            viewModel.onSaveButtonClicked(displayName = binding.etDisplayName.text.toString())
        }


        binding.etDisplayName.doOnTextChanged { text, _, _, _ ->
            viewModel.onDisplayNameChanged(text.toString())
        }

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_IMAGE)?.observe(
            viewLifecycleOwner
        ) { image ->
            viewModel.onImageURLChanged(image)
            binding.imgProfile.setImageURI(Uri.parse(image))
        }

        viewModel.onViewCreated(this)
    }

    override fun navigateToAddPic() {
        findNavController().navigate(R.id.action_editProfile_to_addPicBottomSheet)
    }

    override fun navigateToProfile(user: User) {
        setNavigationResult(KEY_ARGUMENT_UPDATE_USER, user)
        findNavController().popBackStack()
    }
}



