package com.example.main_project.SettingProfile.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.ProfilePictureResponse
import com.example.main_project.SettingProfile.DataClasses.RequiterRequest
import com.example.main_project.databinding.FragmentRecuriterProfileBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class RecuriterProfile : Fragment() {

    private var _binding: FragmentRecuriterProfileBinding? = null
    private val binding get() = _binding!!

    private val IMAGE_PICK_CODE = 1000
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuriterProfileBinding.inflate(inflater, container, false)

        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, role)
        binding.roleDefine.setAdapter(arrayAdapter)

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                sendRecruiterData()
            }
        }

        binding.pic.setOnClickListener {
            pickImageFromGallery()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.recuriterProfile)
            }
        })

        setFocusChangeListener(binding.companyName)
        setFocusChangeListener(binding.companyEmail)
        setFocusChangeListener(binding.JobTitle)
        setFocusChangeListener(binding.companyWebsite)
        setFocusChangeListener(binding.industry)

        return binding.root
    }

    private fun setFocusChangeListener(editText: TextInputLayout) {
        editText.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(editText)
        }
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
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.companyEmail.editText?.text.toString().trim()
        val yearOfWork = binding.JobTitle.editText?.text.toString().trim()
        val DOB = binding.companyWebsite.editText?.text.toString().trim()
        val industry = binding.industry.editText?.text.toString().trim()

        var isValid = true

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.companyName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.companyEmail)
            isValid = false
        }

        if (yearOfWork.isBlank()) {
            setToErrorDrawable(binding.JobTitle)
            isValid = false
        }

        if (DOB.isBlank()) {
            setToErrorDrawable(binding.companyWebsite)
            isValid = false
        }

        if (industry.isBlank()) {
            setToErrorDrawable(binding.industry)
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
                    imageUri?.let { uploadProfilePicture(it) }
                    Toast.makeText(requireContext(), "Success: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.recruiterDetails)
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

    private fun uploadProfilePicture(imageUri: Uri) {
        lifecycleScope.launch {
            try {
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)

                val file = File(getRealPathFromURI(imageUri))
                if (!file.exists()) {
                    Toast.makeText(requireContext(), "File does not exist", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", file.name, requestBody)

                val response: Response<ProfilePictureResponse> = api.uploadProfileRecruiterPicture(part)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "Failed to upload image"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                println(e.message)
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex("_data")
        val path = columnIndex?.let { cursor.getString(it) }

        cursor?.close()

        return path ?: ""
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri = data?.data
            if (imageUri != null) {
                this.imageUri = imageUri
                binding.pic.setImageURI(imageUri)

                binding.pic.setContentPadding(0, 0, 0, 0)

                uploadProfilePicture(imageUri)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
