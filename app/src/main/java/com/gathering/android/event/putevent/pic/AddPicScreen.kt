package com.gathering.android.event.putevent.pic

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenAddPicBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.ui.theme.GatheringTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddPicScreen : BottomSheetDialogFragment(), AddPicNavigator {

    private lateinit var binding: ScreenAddPicBinding

    @Inject
    lateinit var viewModel: AddPicViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenAddPicBinding.inflate(LayoutInflater.from(requireContext()))
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
                            AddPicScreenWithCompose(
                                errorMessage = state.value.errorMessage ?: "",
                                onCameraClick = viewModel::onCameraClicked,
                                onGalleryClick = viewModel::onGalleryClicked
//                                onOkClick = viewModel::onSaveButtonClicked,
//                                onRotateImageClick = viewModel::onRotateClicked
                            )
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

                    state.rotatedImage?.let { rotatedImage ->
                        binding.imageView.setImageBitmap(rotatedImage)
                    }

                    state.errorMessage?.let {
                        showErrorText(it)
                    }

                    state.showImage?.let { image ->
                        binding.imageView.setImageBitmap(image)
                    }
                }
            }
            binding.btnCamera.setOnClickListener {
                PermissionX.init(this).permissions(Manifest.permission.CAMERA)
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            viewModel.onCameraClicked()
                        } else {
                            showErrorText(CAMERA_PERMISSION_DENIED)
                        }
                    }
            }
            binding.btnGallery.setOnClickListener {
                viewModel.onGalleryClicked()
            }

            binding.rotateImage.setOnClickListener {
                val bitmap = binding.imageView.getBitmap()
                if (bitmap != null) {
                    viewModel.onRotateClicked(bitmap, -90f)
                } else showErrorText(SELECT_VALID_PICTURE)
            }
            binding.btnOk.setOnClickListener {
                val image = binding.imageView.getBitmap()
                if (image != null) {
                    viewModel.onSaveButtonClicked(image)
                } else {
                    showErrorText(SELECT_VALID_PICTURE)
                }
            }
            viewModel.onViewCreated(this)
        }
    }

    private fun ImageView.getBitmap(): Bitmap? {
        return (drawable as? BitmapDrawable)?.bitmap
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onImageSelectedFromGallery(data)
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onImageSelectedFromCamera(data?.extras?.get("data") as Bitmap)
        } else {
            showErrorText(OPERATION_CANCELED_BY_USER)
        }
    }


    override fun navigateToAddEvent(imagePath: String) {
        setNavigationResult(KEY_ARGUMENT_SELECTED_IMAGE, imagePath)
        findNavController().popBackStack()
    }

    override fun navigateToCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    override fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val OPERATION_CANCELED_BY_USER = "Operation cancelled by user"
        private const val CAMERA_PERMISSION_DENIED = "camera permission denied"
        const val SELECT_VALID_PICTURE = "SELECT A VALID PICTURE PLEASE"
    }

    @Composable
    @Preview(showBackground = true, device = "id:Nexus S")
    fun AddPicScreenPreview() {
        AddPicScreenWithCompose("", {}, {})
    }

    @Composable
    fun AddPicScreenWithCompose(
        errorMessage: String,
//        onRotateImageClick: () -> Unit,
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

        }
    }
}