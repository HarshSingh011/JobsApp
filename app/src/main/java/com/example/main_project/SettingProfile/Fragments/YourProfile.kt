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
import com.example.main_project.DataStoreManager
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.SettingProfile.DataClasses.Education
import com.example.main_project.SettingProfile.DataClasses.Experience
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
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourProfileBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())

        binding.addEducation.setOnClickListener {
            addDataToCandidateData()
        }

        binding.addExperience.setOnClickListener {
            addExperienceDataToList()
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

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                sendCandidateData()
                showLoadingDialog()
                println("hello world")
            }
        }

        val domainSuggestions = listOf(
            "Web Developer", "Front End", "Back End", "Data Scientist",
            "Software Engineer", "Android Developer", "Game Developer","Weblogic Admin",
            "Java", "Kotlin", "Python", "JavaScript", "Ruby", "C++", "React", "Spring Boot", "AWS", "Angular", "Vue.js",
            "Node.js", "Express.js", "MongoDB", "SQL", "NoSQL", "Git", "Docker", "Kubernetes", "Machine Learning",
            "Artificial Intelligence", "Data Science", "DevOps", "Agile", "Scrum", "iOS", "Android", "Swift", "Flutter",
            "React Native", "GraphQL", "RESTful API", "Microservices", "Blockchain", "Cybersecurity", "Cloud Computing"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, domainSuggestions)
        binding.autoCompleteDomain.setAdapter(adapter)


        binding.instituteName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.instituteName)
        }
        binding.Degree.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.Degree)
        }
        binding.yearOfCompleletion.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.yearOfCompleletion)
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
            }
        })

        return binding.root
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.settingprofiledialog)
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
        val institutename = binding.instituteName.editText?.text.toString().trim()
        val position = binding.Degree.editText?.text.toString().trim()
        val yearOfCompleletion = binding.yearOfCompleletion.editText?.text.toString().trim()
        val domain = binding.autoCompleteDomain.text.toString().trim()
        val companyname = binding.companyName.editText?.text.toString().trim()
        val degree = binding.Degree.editText?.text.toString().trim()
        val  yearOfWork= binding.yearOfWork.editText?.text.toString().trim()


        var isValid = true

        if (institutename.isBlank()) {
            setToErrorDrawable(binding.instituteName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.Degree)
            isValid = false
        }

        if (yearOfCompleletion.isBlank()) {
            setToErrorDrawable(binding.yearOfCompleletion)
            isValid = false
        }else{
            val yearRegex = "^(19|20)\\d{2}$".toRegex()
            if (yearOfCompleletion.isBlank() || !yearRegex.matches(yearOfCompleletion)) {
                binding.yearOfCompleletion.editText?.setBackgroundResource(R.drawable.error_prop)
                binding.yearOfCompleletion.error = "YYYY"
                binding.yearOfCompleletion.clearFocus()
                isValid = false
            }
        }

        if (yearOfWork.isNotBlank() && !yearOfWork.matches(Regex("\\d{2}"))) {
            binding.yearOfWork.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.yearOfWork.error = "YY"
            binding.yearOfWork.clearFocus()
            isValid = false
        }

        return isValid
    }

    private fun sendCandidateData() {
        lifecycleScope.launch {
            try {
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)

                val candidateData = CandidateData(
                    education = sharedViewModel.educationList,
                    experience = sharedViewModel.experienceList,
                    skill = listOf(sharedViewModel.domain),
                    DOB = sharedViewModel.DOB
                )

                println(candidateData)

                val response = api.createCandidate(candidateData)
                if (response.isSuccessful) {
                    loadingDialog.dismiss()
                    val role = "CANDIDATE"
                    dataStoreManager.saveRole(role)
                    Toast.makeText(context, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.certificates)
                    sharedViewModel.isApiSuccess = true
                } else {
                    loadingDialog.dismiss()
                    val errorResponse = response.errorBody()?.string()
                    println("failer")
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                println(e.message)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun addDataToCandidateData() {
        val institution = binding.instituteName.editText?.text.toString().trim()
        val degree = binding.Degree.editText?.text.toString().trim()
        val yearOfCompletion = binding.yearOfCompleletion.editText?.text.toString().trim().toIntOrNull()
        val newEducation = Education(institution, degree, yearOfCompletion)
        sharedViewModel.educationList.add(newEducation)
        Toast.makeText(requireContext(), "Education added", Toast.LENGTH_SHORT).show()

        println(sharedViewModel.educationList)
        println(sharedViewModel.domain)
    }

    private fun addExperienceDataToList() {
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.position.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfWork.editText?.text.toString().trim()

        Toast.makeText(requireContext(), "Experience added", Toast.LENGTH_SHORT).show()
        val experience = Experience(companyName, yearOfWork.toInt(), position)

        sharedViewModel.experienceList.add(experience)

        println(sharedViewModel.educationList)
        println(sharedViewModel.experienceList)
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
