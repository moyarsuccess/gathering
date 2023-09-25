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
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenAddPicBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
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
        binding = ScreenAddPicBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->

                if (state.isInProgress) {
                    binding.btnOk.startAnimation()
                } else {
                    binding.btnOk.revertAnimation()
                }

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
        val bundle = bundleOf(KEY_ARGUMENT_SELECTED_IMAGE to imagePath)
        findNavController().navigate(R.id.action_back_to_add_event, bundle)
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
}