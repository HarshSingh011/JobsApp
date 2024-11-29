package com.example.main_project.Recruiter.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.Recruiter.DataClasses.ApplicantsResponse
import com.example.main_project.adapters.RecruiterApplicantsAdapter
import com.example.main_project.databinding.FragmentApplicantsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Applicants : Fragment() {

    private var _binding: FragmentApplicantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var applicantsAdapter: RecruiterApplicantsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicantsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        // Retrieve job ID from the arguments passed from the previous fragment
        val jobId = arguments?.getLong("jobId")

        jobId?.let {
            fetchApplicants(it)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewApplicants.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchApplicants(jobId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiClient = CandidateProfileRetrofitClient.instance(requireContext())
                    .create(CandidateInterface::class.java)

                val response = apiClient.getJobApplications(jobId)

                if (response.isSuccessful) {
                    val applicants = response.body() ?: emptyList()

                    withContext(Dispatchers.Main) {
                        // Pass the context along with the applicants list to the adapter
                        applicantsAdapter = RecruiterApplicantsAdapter(applicants, requireContext())
                        binding.recyclerViewApplicants.adapter = applicantsAdapter
                    }
                } else {
                    // Handle API errors
                }
            } catch (e: Exception) {
                // Handle exceptions, like network failures
                e.printStackTrace()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
