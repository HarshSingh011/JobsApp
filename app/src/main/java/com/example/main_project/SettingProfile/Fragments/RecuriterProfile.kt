package com.example.main_project.SettingProfile.Fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.DataStoreManager
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.RequiterRequest
import com.example.main_project.databinding.FragmentRecuriterProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import android.view.ViewTreeObserver

class RecuriterProfile : Fragment() {

    private var _binding: FragmentRecuriterProfileBinding? = null
    private val binding get() = _binding!!

    private val IMAGE_PICK_CODE = 1000
    private var imageUri: Uri? = null
    private lateinit var loadingDialog: Dialog
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuriterProfileBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                sendRecruiterData()
            }
        }

        binding.pic.setOnClickListener {
            pickImageFromGallery()
        }

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                sendRecruiterData()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.recuriterProfile)
            }
        })

        binding.companyName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.companyName)
        }
        binding.companyEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.companyEmail)
        }
        binding.JobTitle.editText?.setOnFocusChangeListener() {_, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.JobTitle)
        }
        binding.companyWebsite.editText?.setOnFocusChangeListener(){_, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.companyWebsite)
        }
        binding.industry.editText?.setOnFocusChangeListener(){_, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.industry)
        }
        binding.location.editText?.setOnFocusChangeListener(){_, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.location)
        }

        return binding.root
    }

    private fun navigateToNotification5() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://mainActivity5/recruiterDetails"))
        startActivity(intent)
        requireActivity().finish()
    }

    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
    }

    private fun setToErrorDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.error_prop)
        editText.error = "Empty Field!"
        editText.clearFocus()
    }

    private fun validateInputs(): Boolean {
        val location = binding.location.editText?.text.toString().trim()
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.companyEmail.editText?.text.toString().trim()
        val jobtitle = binding.JobTitle.editText?.text.toString().trim()
        val companywebsite = binding.companyWebsite.editText?.text.toString().trim()
        val industry = binding.industry.editText?.text.toString().trim()
        val email = binding.companyEmail.editText?.text.toString().trim()

        var isValid = true

        if(email.isBlank()){
            setToErrorDrawable(binding.companyEmail)
            isValid = false
        }else{
            if(!email.matches(Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))){
                binding.companyEmail.error = "Not an email"
                binding.companyEmail.editText?.setBackgroundResource(R.drawable.error_prop)
                binding.companyEmail.clearFocus()
                isValid = false
            }
        }

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.companyName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.companyEmail)
            isValid = false
        }

        if (jobtitle.isBlank()) {
            setToErrorDrawable(binding.JobTitle)
            isValid = false
        }

        if (companywebsite.isBlank()) {
            setToErrorDrawable(binding.companyWebsite)
            isValid = false
        }else{
            if(!companywebsite.matches(Regex("^(www\\.)[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$"))){
                binding.companyWebsite.error = "Not a valid website"
                binding.companyWebsite.editText?.setBackgroundResource(R.drawable.error_prop)
                binding.companyWebsite.clearFocus()
                isValid = false
            }
        }

        if (industry.isBlank()) {
            setToErrorDrawable(binding.industry)
            isValid = false
        }

        if( location.isBlank()){
            setToErrorDrawable(binding.location)
            isValid = false
        }

        return isValid
    }

    private fun sendRecruiterData() {
        val recruiterRequest = RequiterRequest(
            companyName = binding.companyName.editText?.text.toString().trim(),
            companyEmail = binding.companyEmail.editText?.text.toString().trim(),
            jobTitle = binding.JobTitle.editText?.text.toString().trim(),
            companyWebsite = binding.companyWebsite.editText?.text.toString().trim(),
            industry = binding.industry.editText?.text.toString().trim()
        )

        lifecycleScope.launch {
            try {
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)
                val response = api.createRecruiter(recruiterRequest)

                if (response.isSuccessful) {
                    val role = "RECRUITER"
                    findNavController().navigate(R.id.mainActivity5)
                    dataStoreManager.saveRole(role)
                    imageUri?.let { uploadProfilePicture(it) }
                    Toast.makeText(requireContext(), "Success: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    println("Success: ${response.body()?.message}")
                    navigateToNotification5()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "HTTP Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unexpected Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfilePicture(fileUri: Uri) {
        val context = requireContext()
        showLoadingDialog()
        lifecycleScope.launch {
            try {

                val filePath = getActualFilePath(fileUri)

                if (filePath == null) {
                    Toast.makeText(context, "Unable to retrieve file path", Toast.LENGTH_LONG).show()
                    Log.e("ProfilePictureUpload", "File path retrieval failed")
                    return@launch
                }

                val file = File(filePath)


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
            binding.pic.setImageURI(fileUri)
            binding.pic.setContentPadding(0, 0, 0, 0)
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

