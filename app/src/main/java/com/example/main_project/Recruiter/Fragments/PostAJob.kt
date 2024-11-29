package com.example.main_project.Recruiter.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.JobRequest
import com.example.main_project.databinding.FragmentPostAJobBinding
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

        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.jobPosted)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostAJobBinding.inflate(inflater, container, false)

        val skillSuggestions = listOf(
            "Java", "Kotlin", "Python", "JavaScript", "Ruby", "C++", "React", "Spring Boot", "AWS", "Angular", "Vue.js"
        )
        val skillAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, skillSuggestions)
        binding.skillInputbox.setAdapter(skillAdapter)

        binding.skillInput.setEndIconOnClickListener {
            val enteredSkill = binding.skillInputbox.text.toString().trim()
            if (enteredSkill.isNotEmpty()) {
                requiredSkills.add(enteredSkill)
                binding.skillInputbox.setText("")
                Toast.makeText(requireContext(), "Skill added: $enteredSkill", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a skill to add", Toast.LENGTH_SHORT).show()
            }
        }

        binding.submit.setOnClickListener {
            lifecycleScope.launch {
                postJob()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun postJob() {
        val title = binding.jobtitleInputbox.text.toString()
        val description = binding.DescriptionInputbox.text.toString()
        val location = binding.locationInputbox.text.toString()
        val experience = binding.ExperienceInputbox.text.toString().toIntOrNull() ?: 0
        val minSalary = binding.MinimumSalaryInputbox.text.toString().toIntOrNull() ?: 0
        val maxSalary = binding.MaximumSalaryInputbox.text.toString().toIntOrNull() ?: 0
        val jobType = binding.JobTypeInputbox.text.toString()
        val jobMode = binding.ModeInputbox.text.toString()

        if (title.isBlank() || description.isBlank() || location.isBlank() || jobType.isBlank() || requiredSkills.isEmpty()) {
            println("Title: $title, Description: $description, Location: $location, Job Type: $jobType, Required Skills: $requiredSkills")
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val jobRequest = JobRequest(
            title = title,
            description = description,
            location = location,
            experience = experience,
            minSalary = minSalary,
            maxSalary = maxSalary,
            jobType = jobType,
            jobMode = jobMode,
            requiredSkills = requiredSkills
        )

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
                val errorBody = response.errorBody()?.string()
                Log.e("PostAJob", "Error Body: $errorBody")
                val errorMessage = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    "Unknown error occurred: ${response.code()}"
                }
                Log.e("PostAJob", "API Error: $errorMessage")
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("PostAJob", "Exception: ${e.message}", e)
            Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
