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
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.BottomSheetAddPicBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddPicBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddPicBinding

    @Inject
    lateinit var viewModel: AddPicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddPicBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewState()
    }

    private fun setupClickListeners() {
        binding.btnCamera.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
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

        binding.rotateLeft.setOnClickListener {
            viewModel.onRotateClicked(binding.imageView.getBitmap(), -90f)
        }

        binding.rotateRight.setOnClickListener {
            viewModel.onRotateClicked(binding.imageView.getBitmap(), 90f)
        }

        binding.btnOk.setOnClickListener {
            viewModel.onOKButtonClicked(binding.imageView.getBitmap())
        }
    }

    private fun ImageView.getBitmap(): Bitmap {
        return (drawable as BitmapDrawable).bitmap
    }

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddPicViewState.ShowCamera -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }

                is AddPicViewState.ShowGallery -> {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, GALLERY_REQUEST_CODE)
                }

                is AddPicViewState.SetResultAndClose -> {
                    setNavigationResult(KEY_ARGUMENT_SELECTED_IMAGE, state.imagePath)
                    findNavController().popBackStack()
                }

                is AddPicViewState.ShowImage -> {
                    binding.imageView.setImageBitmap(state.image)
                }

                is AddPicViewState.Error -> {
                    showErrorText(state.error)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onImageSelectedFromGallery(data)
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onImageSelectedFromCamera(data)
        } else {
            showErrorText(OPERATION_CANCELED_BY_USER)
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val OPERATION_CANCELED_BY_USER = "Operation cancelled by user"
        private const val CAMERA_PERMISSION_DENIED = "camera permission denied"
    }
}