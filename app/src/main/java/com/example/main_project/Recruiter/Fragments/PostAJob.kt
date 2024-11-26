package com.example.main_project.Recruiter.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.databinding.FragmentPostAJobBinding
import com.example.main_project.Recruiter.DataClasses.JobRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAJob : Fragment() {

    private var _binding: FragmentPostAJobBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostAJobBinding.inflate(inflater, container, false)

        binding.submit.setOnClickListener {
            postJob()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun postJob() {
        val title = binding.jobtitleInputbox.text.toString()
        val description = binding.DescriptionInputbox.text.toString()
        val location = binding.locationInputbox.text.toString()
        val experience = binding.ExperienceInputbox.text.toString().toIntOrNull() ?: 0
        val minSalary = binding.MinimumSalaryInputbox.text.toString().toIntOrNull() ?: 0
        val maxSalary = binding.MaximumSalaryInputbox.text.toString().toIntOrNull() ?: 0
        val employmentType = binding.ExperienceInputbox.text.toString()
        val requiredSkills = binding.skillInputbox.text.toString().split(",").map { it.trim() }

        if (title.isBlank() || description.isBlank() || location.isBlank() || employmentType.isBlank()) {
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
            employmentType = employmentType,
            requiredSkills = requiredSkills
        )

        CoroutineScope(Dispatchers.IO).launch {
            val api = CandidateProfileRetrofitClient.instance(requireContext())
                .create(CandidateInterface::class.java)

            try {
                val response = api.postJob(jobRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to post job", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
