package com.example.main_project.Recruiter.Fragments

import android.os.Bundle
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
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.JobRequest
import com.example.main_project.databinding.FragmentPostAJobBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.json.JSONObject

class PostAJob : Fragment() {

    private var _binding: FragmentPostAJobBinding? = null
    private val binding get() = _binding!!

    private var requiredSkills = mutableListOf<String>()

    override fun onResume() {
        super.onResume()

        val jobType = resources.getStringArray(R.array.JobType)
        val arrayAdapterJobType = ArrayAdapter(requireContext(), R.layout.dropdownmenu, jobType)
        binding.JobTypeInputbox.setAdapter(arrayAdapterJobType)

        val jobMode = resources.getStringArray(R.array.JobMode)
        val arrayAdapterJobMode = ArrayAdapter(requireContext(), R.layout.dropdownmenu, jobMode)
        binding.ModeInputbox.setAdapter(arrayAdapterJobMode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostAJobBinding.inflate(inflater, container, false)

        val skillSuggestions = listOf(
            "Java", "Kotlin", "Python", "JavaScript", "Ruby", "C++", "React", "Spring Boot", "AWS", "Angular", "Vue.js",
            "Node.js", "Express.js", "MongoDB", "SQL", "NoSQL", "Git", "Docker", "Kubernetes", "Machine Learning",
            "Artificial Intelligence", "Data Science", "DevOps", "Agile", "Scrum", "iOS", "Android", "Swift", "Flutter",
            "React Native", "GraphQL", "RESTful API", "Microservices", "Blockchain", "Cybersecurity", "Cloud Computing"
        )
        val skillAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, skillSuggestions)
        binding.skillInputbox.setAdapter(skillAdapter)

        binding.skillInput.setEndIconOnClickListener {
            val enteredSkill = binding.skillInputbox.text.toString().trim()
            if (enteredSkill.isNotEmpty()) {
                requiredSkills.add(enteredSkill)
                binding.skillInputbox.text?.clear()
                Toast.makeText(requireContext(), "Skill added: $enteredSkill", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a skill to add", Toast.LENGTH_SHORT).show()
            }
        }

        binding.JobTypeInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.JobTypeInput)
        }
        binding.locationInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.locationInput)
        }
        binding.ExperienceInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.ExperienceInput)
        }
        binding.jobtitleInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.jobtitleInput)
        }
        binding.ModeInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.ModeInput)
        }
        binding.MinimumSalaryInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.MinimumSalaryInput)
        }
        binding.MaximumSalaryInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.MaximumSalaryInput)
        }
        binding.DescriptionInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.DescriptionInput)
        }
        binding.skillInput.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.skillInput)
        }

        binding.submit.setOnClickListener {
            if (validateInputs()) {
                lifecycleScope.launch {
                    postJob()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.postAJob)
            }
        })

        return binding.root
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
        val jobtitle = binding.jobtitleInput.editText?.text.toString().trim()
        val location = binding.locationInput.editText?.text.toString().trim()
        val experience = binding.ExperienceInput.editText?.text.toString().trim()
        val jobtype = binding.JobTypeInput.editText?.text.toString().trim()
        val mode = binding.ModeInput.editText?.text.toString().trim()
        val minimumsal = binding.MinimumSalaryInput.editText?.text.toString().trim()
        val maximumsal = binding.MaximumSalaryInput.editText?.text.toString().trim()
        val des = binding.DescriptionInput.editText?.text.toString().trim()
        val skill = binding.skillInput.editText?.text.toString().trim()

        var isValid = true

        if (maximumsal.isBlank()) {
            setToErrorDrawable(binding.MaximumSalaryInput)
            isValid = false
        }

        if (des.isBlank()) {
            setToErrorDrawable(binding.DescriptionInput)
            isValid = false
        }

        if( skill.isBlank()){
            setToErrorDrawable(binding.skillInput)
            isValid = false
        }

        if( minimumsal.isBlank()){
            setToErrorDrawable(binding.MinimumSalaryInput)
            isValid = false
        }

        if( mode.isBlank()){
            setToErrorDrawable(binding.ModeInput)
            isValid = false
        }

        if (location.isBlank()) {
            setToErrorDrawable(binding.locationInput)
            isValid = false
        }

        if (jobtitle.isBlank()) {
            setToErrorDrawable(binding.jobtitleInput)
            isValid = false
        }

        if (experience.isBlank()) {
            setToErrorDrawable(binding.ExperienceInput)
            isValid = false
        }

        if (jobtype.isBlank()) {
            setToErrorDrawable(binding.JobTypeInput)
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun postJob() {
        val title = binding.jobtitleInputbox.text.toString().trim()
        val description = binding.DescriptionInputbox.text.toString().trim()
        val location = binding.locationInputbox.text.toString().trim()
        val experience = binding.ExperienceInputbox.text.toString().trim().toIntOrNull() ?: 0
        val minSalary = binding.MinimumSalaryInputbox.text.toString().trim().toIntOrNull() ?: 0
        val maxSalary = binding.MaximumSalaryInputbox.text.toString().trim().toIntOrNull() ?: 0
        val jobType = binding.JobTypeInputbox.text.toString().trim()
        val jobMode = binding.ModeInputbox.text.toString().trim()

        val jobRequest = JobRequest(
            title = title,
            description = description,
            location = location,
            experience = experience,
            minSalary = minSalary,
            maxSalary = maxSalary,
            jobType = jobType,
            mode = jobMode,
            requiredSkills = requiredSkills
        )

        println(jobRequest)

        val api = CandidateProfileRetrofitClient.instance(requireContext())
            .create(CandidateInterface::class.java)

        try {
            Log.d("PostAJob", "Sending job request: $jobRequest")
            val response = api.postJob(jobRequest)

            Log.d("PostAJob", "Response: $response")
            Log.d("PostAJob", "Response Body: ${response.body()}")
            Log.d("PostAJob", "Response Code: ${response.code()}")
            Log.d("PostAJob", "Response Headers: ${response.headers()}")

            if (response.isSuccessful) {
                Toast.makeText(requireContext(), response.body()?.message ?: "Success", Toast.LENGTH_SHORT).show()
            } else {
                val errorResponse = response.errorBody()?.string()
                val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                println(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("PostAJob", "Exception: ${e.message}", e)
            Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
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
}