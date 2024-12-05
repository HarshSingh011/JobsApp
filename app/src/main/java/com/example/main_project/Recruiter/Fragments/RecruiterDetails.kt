package com.example.main_project.Recruiter.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.main_project.SettingProfile.ViewModel.RecruiterViewModel
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.CandidateInterface
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.RecruiterData
import com.example.main_project.databinding.FragmentRecruiterDetailsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class RecruiterDetails : Fragment() {

    private var _binding: FragmentRecruiterDetailsBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private lateinit var loadingDialog: Dialog
    private val recruiterViewModel: RecruiterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecruiterDetailsBinding.inflate(inflater, container, false)

        fetchRecruiterData(requireContext())

        recruiterViewModel.recruiterData.observe(viewLifecycleOwner, Observer { recruiterData ->
            binding.firstname.text = recruiterData.firstName ?: "N/A"
            binding.lastname.text = recruiterData.lastName ?: "N/A"
            binding.Email.text = recruiterData.email ?: "N/A"
            binding.CompanyName.text = recruiterData.companyName ?: "N/A"
            binding.phonenumber.text = recruiterData.phone ?: "N/A"
            binding.CompanyEmail.text = recruiterData.companyEmail ?: "N/A"
            binding.JobTitle.text = recruiterData.jobTitle ?: "N/A"
            binding.CompanyWebsite.text = recruiterData.companyWebsite ?: "N/A"
            binding.CompanyLocation.text = recruiterData.companyLocation ?: "N/A"
            binding.Industry.text = recruiterData.industry ?: "N/A"

            if (!recruiterData.profileImage.isNullOrEmpty()) {
                Glide.with(this)
                    .load(recruiterData.profileImage)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_launcher_background)
            }
        })

        binding.editData.setOnClickListener(){
            findNavController().navigate(R.id.recruiterDetailsEdit)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.recruiterDetails)
            }
        })

        binding.profileImage.setOnClickListener {
            pickImageFromGallery()
        }

        return binding.root
    }

    private fun fetchRecruiterData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = CandidateProfileRetrofitClient.instance(context).create(CandidateInterface::class.java)
                val response: Response<RecruiterData> = service.getCurrentRecruiter()

                if (response.isSuccessful) {
                    response.body()?.let {
                        recruiterViewModel.saveRecruiterData(it)
                    }
                    imageUri?.let { uploadProfilePicture(it) }
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadProfilePicture(fileUri: Uri) {
        val context = requireContext()
        showLoadingDialog()
        lifecycleScope.launch {
            try {
                Log.d("ProfilePictureUpload", "Original URI: $fileUri")
                Log.d("ProfilePictureUpload", "URI Scheme: ${fileUri.scheme}")
                Log.d("ProfilePictureUpload", "URI Path: ${fileUri.path}")

                val filePath = getActualFilePath(fileUri)

                if (filePath == null) {
                    Toast.makeText(context, "Unable to retrieve file path", Toast.LENGTH_LONG).show()
                    Log.e("ProfilePictureUpload", "File path retrieval failed")
                    return@launch
                }

                val file = File(filePath)

                Log.d("ProfilePictureUpload", "Resolved File Path: ${file.absolutePath}")
                Log.d("ProfilePictureUpload", "File Exists: ${file.exists()}")
                Log.d("ProfilePictureUpload", "File Length: ${file.length()} bytes")

                if (!file.exists()) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(fileUri) ?: "image/jpeg"

                Log.d("ProfilePictureUpload", "Detected MIME Type: $mimeType")

                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val api = CandidateProfileRetrofitClient.instance(context).create(CandidateInterface::class.java)
                val response = api.uploadProfileRecruiterPicture(body)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Image uploaded successfully!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    Log.d("ProfilePictureUpload", "Response: $message")
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse).optString("message", "Failed to upload image")
                    } catch (e: Exception) {
                        "Failed to upload image"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("ProfilePictureUpload", "Error Response Code: ${response.code()}")
                    Log.e("ProfilePictureUpload", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("API Error", "Exception details", e)
            } finally {
                loadingDialog.dismiss()
            }
        }
    }


    private fun getActualFilePath(uri: Uri): String? {
        val context = requireContext()
        return when {
            "content" == uri.scheme -> {
                try {
                    val projection = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = context.contentResolver.query(uri, projection, null, null, null)

                    cursor?.use {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        it.moveToFirst()
                        it.getString(columnIndex)
                    }
                } catch (e: Exception) {
                    uriToFileWithContentResolver(uri)
                }
            }

            "file" == uri.scheme -> uri.path

            else -> {
                Log.e("FileUpload", "Unsupported URI scheme: ${uri.scheme}")
                null
            }
        }
    }

    private fun uriToFileWithContentResolver(uri: Uri): String? {
        val context = requireContext()
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile.absolutePath
        } catch (e: Exception) {
            Log.e("FileUpload", "Error converting URI to file: ${e.message}")
            return null
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val context = requireContext()
        val contentResolver = context.contentResolver

        if (uri.scheme == "file") {
            return uri.path
        }

        if (uri.scheme == "content") {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        }

        return null
    }

    private fun pickImageFromGallery() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileUri = data?.data
        if (fileUri != null) {
            binding.profileImage.setImageURI(fileUri)
            binding.profileImage.setContentPadding(0, 0, 0, 0)
            imageUri = fileUri
            uploadProfilePicture(fileUri)
        } else {
            Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseErrorMessage(response: String?): String {
        return try {
            val jsonObject = JSONObject(response ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "An error occurred"
        }
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.loader)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingDialog.setCancelable(false)
        }
        loadingDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
