package com.example.main_project.SeeJobs.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.JobApplyResponse
import com.example.main_project.databinding.FragmentJobsDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class JobsDetails : Fragment() {

    private var _binding: FragmentJobsDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobsDetailsBinding.inflate(inflater, container, false)

        val jobDescription = arguments?.getString("job_description")
        binding.Description.text = jobDescription

        val jobTitle = arguments?.getString("job_title")
        binding.Title.text = jobTitle

        val jobLocation = arguments?.getString("job_location")
        binding.location.text = jobLocation

        val jobSalary = arguments?.getString("job_salary")
        binding.Salary.text = jobSalary

        val jobMode = arguments?.getString("job_mode")
        binding.JobMode.text = jobMode

        val jobType = arguments?.getString("job_type")
        binding.JobType.text = jobType

        val jobId = arguments?.getString("job_id")

        binding.apply.setOnClickListener {
            jobId?.let {
                applyForJob(it)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.searchJob)
                }
            }
        )


        return binding.root
    }

    private fun applyForJob(id: String) {
        println("Applying for job with ID: $id")
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<JobApplyResponse> = apiService.applyForJob(id)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    println("Applied for job successfully")
                } else {
                    Log.e("API Error", "Error applying for job: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to apply for the job.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API Error", "Exception: ${e.message}")
                Toast.makeText(requireContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}