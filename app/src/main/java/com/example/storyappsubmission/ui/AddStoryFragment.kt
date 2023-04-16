package com.example.storyappsubmission.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.remote.result.Result
import com.example.storyappsubmission.ViewModel.StoryViewModel
import com.example.storyappsubmission.ViewModel.StoryViewModelFactory
import com.example.storyappsubmission.databinding.FragmentAddStoryBinding
import com.example.storyappsubmission.utils.rotateFile
import com.example.storyappsubmission.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddStoryFragment : Fragment() {
    private lateinit var binding: FragmentAddStoryBinding
    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.GONE
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }
        binding.takeCamera.setOnClickListener { startCamera() }
        binding.takeGallery.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { upload() }
        return binding.root
    }

    private fun startCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Sebuah Gambar")
        launcherIntentGallery.launch(chooser)

    }

    private fun upload() {
        val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(requireContext())
        val viewModel: StoryViewModel by viewModels {
            factory
        }

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionval = binding.description.text
            val description = "$descriptionval".toRequestBody("text/plain".toMediaTypeOrNull())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModel.uploadStory(imageMultipart, description)
                .observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                view?.findNavController()
                                    ?.navigate(R.id.action_addStoryFragment_to_storyListFragment)
                                Toast.makeText(context, result.data.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                }

        } else {
            Toast.makeText(context, getString(R.string.empty_image), Toast.LENGTH_SHORT).show()
        }
        if (binding.description.text.isEmpty()) {
            binding.description.requestFocus()
            Toast.makeText(context, getString(R.string.empty_description), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                getFile = myFile
                binding.previewImage.setImageURI(uri)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    context,
                    getString(R.string.revoked_permission),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPictByteArray = bmpStream.toByteArray()
            streamLength = bmpPictByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }


}