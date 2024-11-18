package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.databinding.FragmentSearchJobBinding

class SearchJob : Fragment() {

    private var _binding: FragmentSearchJobBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobAdapter: JobAdapter
    private lateinit var recentJobsAdapter: RecentJobAdapter
    private lateinit var jobList: List<SearchJobData>
    private lateinit var recentJobsList: List<RecentJobsData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchJobBinding.inflate(inflater, container, false)

        setupRecyclerViews()

        return binding.root
    }

    private fun setupRecyclerViews() {
        jobList = listOf(
            SearchJobData("Analytic Data", "Apple Inc.", "Full time", "Offline", "1yr EXP", "$300/Month"),
            SearchJobData("Software Engineer", "Google", "Full time", "Remote", "2yr EXP", "$500/Month"),
            SearchJobData("UI/UX Designer", "Microsoft", "Part time", "Hybrid", "1yr EXP", "$400/Month")
        )

        jobAdapter = JobAdapter(jobList)
        binding.jobsearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = jobAdapter
        }

        recentJobsList = listOf(
            RecentJobsData("Analyst", "Analyze data and generate reports"),
            RecentJobsData("Developer", "Develop software applications"),
            RecentJobsData("Designer", "Design user interfaces and experiences")
        )

        recentJobsAdapter = RecentJobAdapter(recentJobsList)
        binding.recentJobsSearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = recentJobsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
