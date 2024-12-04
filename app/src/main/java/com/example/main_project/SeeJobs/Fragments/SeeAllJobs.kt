package com.example.main_project.SeeJobs.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.SeeJobs.Adapter.SeeAllJobsAdapter
import com.example.main_project.SeeJobs.Adapter.SeeAllJobsFilterAdapter
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.databinding.FragmentSeeAllJobsBinding
import kotlinx.coroutines.launch

class SeeAllJobs : Fragment() {

    private lateinit var binding: FragmentSeeAllJobsBinding
    private lateinit var jobsAdapter: SeeAllJobsAdapter
    private lateinit var filterAdapter: SeeAllJobsFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllJobsBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        fetchJobs("closed")

        return binding.root
    }

    private fun setupRecyclerViews() {
        jobsAdapter = SeeAllJobsAdapter()
        binding.jobRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jobsAdapter
        }

        filterAdapter = SeeAllJobsFilterAdapter { status ->
            fetchJobs(status)
        }
        binding.buttonRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }
    }

    private fun fetchJobs(status: String) {
        val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
        lifecycleScope.launch {
            try {
                val response = apiService.getJobsWithFilters(mapOf("status" to status))
                if (response.isSuccessful) {
                    response.body()?.let { jobs ->
                        jobsAdapter.submitList(jobs)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

