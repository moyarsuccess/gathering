package com.gathering.android.event.putevent.pic

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.composables.ErrorTextView
import com.gathering.android.common.composables.CircularImageView
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenAddPicBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.ui.theme.GatheringTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            AddPicScreenWithCompose(
                                errorMessage = state.value.errorMessage ?: "",
                                onCameraClick = viewModel::onCameraClicked,
                                onGalleryClick = viewModel::onGalleryClicked,
                                imageUri = state.value.showImage,
                                onSaveClick = {
                                    state.value.showImage?.let {
                                        viewModel.onSaveButtonClicked(bitmap = it)
                                    }
                                },
                                onRotateClick = {
                                    state.value.showImage?.let { bitmap ->
                                        viewModel.onRotateClicked(
                                            bitmap, 90F
                                        )
                                    }
                                })
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
    @Preview(showBackground = true, device = "id:pixel_2")
    fun AddPicScreenPreview() {
        AddPicScreenWithCompose("error", null, {}, {}, {}, {})
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun AddPicScreenWithCompose(
        errorMessage: String,
        imageUri: Bitmap?,
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit,
        onSaveClick: () -> Unit,
        onRotateClick: () -> Unit
    ) {
        val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
        val galleryPermissionState =
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotatableImageView(imageUri, onRotateClick)
            ImagePickerButtons(
                cameraPermissionState,
                onCameraClick,
                onGalleryClick,
                galleryPermissionState
            )
            CustomActionButton(
                isLoading = false,
                text = "OK",
                onClick = { onSaveClick() },
                colors = ButtonDefaults.buttonColors(),
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )
            ErrorTextView(error = errorMessage)
        }
    }

    @Composable
    @OptIn(ExperimentalPermissionsApi::class)
    private fun ImagePickerButtons(
        cameraPermissionState: PermissionState,
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit,
        galleryPermissionState: PermissionState
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                if (cameraPermissionState.status.isGranted) {
                    onCameraClick()
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            }) {
                Text(text = "OPEN CAMERA")
            }
            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    onGalleryClick()
                } else {
                    galleryPermissionState.launchPermissionRequest()
                }
            }) {
                Text(text = "OPEN GALLERY")
            }
        }
    }

    @Composable
    private fun RotatableImageView(imageUri: Bitmap?, onRotateClick: () -> Unit) {
        Box {
            IconButton(
                onClick = {
                    if (imageUri != null) {
                        onRotateClick()
                    }
                }, modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RotateRight,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            CircularImageView(bmp = imageUri, size = 200.dp)
        }
    }
}