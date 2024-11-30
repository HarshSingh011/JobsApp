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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.Education
import com.example.main_project.databinding.FragmentYourProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class YourProfile : Fragment() {

    private var _binding: FragmentYourProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingDialog: Dialog
    private val sharedViewModel: CandidateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourProfileBinding.inflate(inflater, container, false)

        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, role)
        binding.roleDefine.setAdapter(arrayAdapter)

        binding.addEducation.setOnClickListener {
            addDataToCandidateData()
        }

        binding.searchBox.setEndIconOnClickListener {
            val enteredDomain = binding.autoCompleteDomain.text.toString().trim()
            if (enteredDomain.isNotEmpty()) {
                if (sharedViewModel.domain.isEmpty()) {
                    sharedViewModel.domain = enteredDomain
                } else {
                    sharedViewModel.domain += ", $enteredDomain"
                    println(sharedViewModel)
                }
                binding.searchBox.editText?.text?.clear()
                Toast.makeText(requireContext(), "Domain added: $enteredDomain", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a domain to add", Toast.LENGTH_SHORT).show()
            }
        }

        if (binding.roleDefine.text.toString() == "Candidate") {
            findNavController().navigate(R.id.yourProfile)
        }
        if (binding.roleDefine.text.toString() == "Recruiter") {
            findNavController().navigate(R.id.mainActivity3)
        }

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 1
            }
        }

        val domainSuggestions = listOf(
            "Web Developer", "Front End", "Back End", "Data Scientist",
            "Software Engineer", "Android Developer", "Game Developer"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, domainSuggestions)
        binding.autoCompleteDomain.setAdapter(adapter)

        binding.role.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.role)
        }
        binding.instituteName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.instituteName)
        }
        binding.Degree.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.Degree)
        }
        binding.yearOfCompleletion.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.yearOfCompleletion)
        }
        binding.DateOfBirth.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.DateOfBirth)
        }

        binding.pic.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 0
            }
        })

        return binding.root
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.loader)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
            loadingDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun validateInputs(): Boolean {
        val companyName = binding.instituteName.editText?.text.toString().trim()
        val position = binding.Degree.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfCompleletion.editText?.text.toString().trim()
        val DOB = binding.DateOfBirth.editText?.text.toString().trim()

        var isValid = true

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.instituteName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.Degree)
            isValid = false
        }

        if (yearOfWork.isBlank()) {
            setToErrorDrawable(binding.yearOfCompleletion)
            isValid = false
        }

        if (DOB.isBlank()) {
            setToErrorDrawable(binding.DateOfBirth)
            isValid = false
        }

        return isValid
    }

    private fun addDataToCandidateData() {
        val institution = binding.instituteName.editText?.text.toString().trim()
        val degree = binding.Degree.editText?.text.toString().trim()
        val yearOfCompletion = binding.yearOfCompleletion.editText?.text.toString().trim().toIntOrNull()
        val DOB = binding.DateOfBirth.editText?.text.toString().trim()
        sharedViewModel.DOB = DOB
        val newEducation = Education(institution, degree, yearOfCompletion)
        sharedViewModel.educationList.add(newEducation)

        println(sharedViewModel.educationList)
        println(sharedViewModel.domain)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileUri = data?.data
        if (fileUri != null) {
            binding.pic.setImageURI(fileUri)
            binding.pic.setContentPadding(0, 0, 0, 0)
            uploadImage(fileUri)
        } else {
            Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
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

    private fun uploadImage(fileUri: Uri) {
        val context = requireContext()
        showLoadingDialog()
        lifecycleScope.launch {
            try {
                loadingDialog.dismiss()
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
                val response = api.uploadProfilePicture(body)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Image uploaded successfully!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    Log.d("ProfilePictureUpload", "Response: $message")
                } else {
                    loadingDialog.dismiss()
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
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("API Error", "Exception details", e)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
