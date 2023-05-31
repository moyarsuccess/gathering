package com.gathering.android.event.myevent.addevent.pic

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.BottomSheetAddPicBinding
import com.gathering.android.event.myevent.addevent.KEY_ARGUMENT_SELECTED_IMAGE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.ByteArrayOutputStream

class AddPicBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddPicBinding
    private val GALLERY_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002
    private val GALLERY_PERMISSION_CODE = 1003
    private val CAMERA_PERMISSION_CODE = 1004
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddPicBinding.inflate(LayoutInflater.from(requireContext()))

        binding.rotateLeft.setOnClickListener {
            rotateImageViewBitmap(-90f)
        }

        binding.rotateRight.setOnClickListener {
            rotateImageViewBitmap(90f)
        }
        binding.btnCamera.setOnClickListener {
            if (allPermissionsGranted(REQUIRED_CAMERA_PERMISSIONS)) {
                openCamera()
            } else {
                requestPermissions(
                    REQUIRED_CAMERA_PERMISSIONS, CAMERA_PERMISSION_CODE
                )
            }
        }

        binding.btnGallery.setOnClickListener {
            if (allPermissionsGranted(REQUIRED_GALLERY_PERMISSIONS)) {
                openGallery()
            } else {
                requestPermissions(
                    REQUIRED_GALLERY_PERMISSIONS, GALLERY_PERMISSION_CODE
                )
            }
        }

        binding.btnOk.setOnClickListener {
            val bitmap = binding.imageView.getBitmap()
            if (bitmap != null) {
                uriString = getImagePath(bitmap)
            }

            setNavigationResult(KEY_ARGUMENT_SELECTED_IMAGE, uriString)
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun ImageView.getBitmap(): Bitmap? {
        return (drawable as? BitmapDrawable)?.bitmap
    }

    private fun rotateImageViewBitmap(degree: Float) {
        val bitmap = binding.imageView.getBitmap() ?: return
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        binding.imageView.setImageBitmap(rotatedBitmap)
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    private fun allPermissionsGranted(permList: Array<String>) = permList.all {
        requireContext().checkSelfPermission(
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var uriString: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.imageView.setImageURI(uri)
                uriString = uri.toString()
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            binding.imageView.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(requireContext(), "operation cancelled by user", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getImagePath(inImage: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            inImage,
            "gathering_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path).toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (allPermissionsGranted(REQUIRED_CAMERA_PERMISSIONS)) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_LONG)
                    .show()
            }
        } else if (requestCode == GALLERY_PERMISSION_CODE) {
            if (allPermissionsGranted(REQUIRED_GALLERY_PERMISSIONS)) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "gallery permission denied", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        private val REQUIRED_GALLERY_PERMISSIONS = mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}


