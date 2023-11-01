package com.gathering.android.profile.editProfile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.CustomActionButton
import com.gathering.android.common.CustomTextField
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.ImageView
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenEditProfileBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_USER
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileScreen : FullScreenBottomSheet(), EditProfileNavigator {

    lateinit var binding: ScreenEditProfileBinding

    @Inject
    lateinit var viewModel: EditProfileViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenEditProfileBinding.inflate(LayoutInflater.from(requireContext()))
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
                            EditProfile(
                                displayName = state.value.displayName,
                                email = state.value.email,
                                imageUri = state.value.imageUri,
                                onImageClicked = viewModel::onImageButtonClicked,
                                onSaveChangeButtonClicked = viewModel::onSaveButtonClicked,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_IMAGE)?.observe(
            viewLifecycleOwner
        ) { photoUri ->
            viewModel.onImageURLChanged(photoUri)
        }

        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                imageLoader.loadImage(state.imageUri, binding.imgProfile)
                binding.tvEmail.text = state.email
                binding.etDisplayName.setText(state.displayName)
                binding.btnSave.isEnabled = state.saveButtonEnable!!
                binding.imgProfile.setImageURI(Uri.parse(state.imageUri))
                if (!state.errorMessage.isNullOrEmpty()) {
                    showErrorText(state.errorMessage)
                }
            }
        }

        binding.imgProfile.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.btnSave.setOnClickListener {
            viewModel.onSaveButtonClicked(
                displayName = binding.etDisplayName.text.toString(),
                imageUrl = ""
            )
        }

        binding.etDisplayName.doOnTextChanged { text, _, _, _ ->
            viewModel.onDisplayNameChanged(text.toString())
        }
        viewModel.onViewCreated(this)
    }

    override fun navigateToAddPic() {
        findNavController().navigate(R.id.action_edit_profile_to_addPicScreen)
    }

    override fun navigateToProfile(user: User) {
        setNavigationResult(KEY_ARGUMENT_UPDATE_USER, user)
        findNavController().popBackStack()
    }

    @Preview(showBackground = true)
    @Composable
    fun EditProfilePreview() {
        EditProfile(
            displayName = "Ani",
            email = "animansoubi@gmail.com",
            imageUri = "",
            onImageClicked = {},
            onSaveChangeButtonClicked = { _, _ -> },
        )
    }

    @Composable
    fun EditProfile(
        displayName: String?,
        email: String?,
        imageUri: String?,
        onImageClicked: () -> Unit,
        onSaveChangeButtonClicked: (displayName: String?, imageUrl: String?) -> Unit,
    ) {

        var displayNameState by rememberSaveable { mutableStateOf(displayName) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageView(imageUri = imageUri, size = 200.dp) {
                onImageClicked()
            }
            CustomTextField(
                value = displayNameState ?: "",
                label = "Display Name",
                onValueChange = {
                    displayNameState = it
                })
            CustomTextField(
                value = email ?: "",
                label = "Email Address",
                enabled = false
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomActionButton(
                text = "Save Changes",
                onClick = {
                    onSaveChangeButtonClicked(displayNameState, imageUri)
                },
                isLoading = false
            )
        }
    }
}



