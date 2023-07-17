package com.gathering.android.profile.updateinfo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.BottomSheetUpdateUserInfoBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateUserInfoFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetUpdateUserInfoBinding

    @Inject
    lateinit var viewModel: UpdateUserInfoViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetUpdateUserInfoBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UpdateUserInfoViewState.NavigateToAddPic -> {
                    findNavController().navigate(R.id.action_personalDataBottomSheetFragment_to_addPicBottomSheet)
                }

                is UpdateUserInfoViewState.NavigateToProfile -> {
                    setNavigationResult(KEY_ARGUMENT_UPDATE_USER, state.user)
                    findNavController().popBackStack()
                }

                is UpdateUserInfoViewState.ShowImage -> {
                    imageLoader.loadImage(state.photo_url, binding.imgProfile)
                }

                is UpdateUserInfoViewState.ShowEmailAddress -> {
                    binding.tvEmail.text =
                        state.emailAddress
                }

                is UpdateUserInfoViewState.ShowError -> {
                    state.errorMessage?.also { showErrorText(it) }
                }

                is UpdateUserInfoViewState.ShowDisplayName -> binding.etDisplayName.setText(state.displayName)
                is UpdateUserInfoViewState.SaveChangesButtonVisibility -> {
                    binding.btnSave.isEnabled = state.isSaveChangesButtonEnabled
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

        viewModel.onViewCreated()
    }
}