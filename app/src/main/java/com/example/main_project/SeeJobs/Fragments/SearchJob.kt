package com.example.main_project.SeeJobs.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.SeeJobs.Adapter.JobAdapter
import com.example.main_project.SeeJobs.Adapter.RecentJobAdapter
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.SettingProfile.DataClasses.RecentJobsData
import com.example.main_project.SeeJobs.DataClasses.SearchJobData
import com.example.main_project.databinding.FragmentSearchJobBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchJob : Fragment() {

    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobList: List<Job>
    private lateinit var jobRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchJobBinding.inflate(inflater, container, false)

        jobRecyclerView = binding.jobsearch
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        fetchJobs()

        return binding.root
    }

    private fun fetchJobs() {
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val jobs = apiService.getJobs()
                jobAdapter = JobAdapter(jobs)
                jobRecyclerView.adapter = jobAdapter
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching jobs: ${e.message}")
            }
        }
    }
}
