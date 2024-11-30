package com.example.main_project.Recruiter.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.databinding.FragmentJobPostedBinding
import com.example.main_project.CandidateInterface
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.JobContent
import com.example.main_project.adapters.RecruiterSeeJobsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobPosted : Fragment() {

    private var _binding: FragmentJobPostedBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobAdapter: RecruiterSeeJobsAdapter
    private var jobList: MutableList<JobContent> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobPostedBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchJobs()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.jobPosted)
            }
        })

        return binding.root
    }

    private fun setupRecyclerView() {
        jobAdapter = RecruiterSeeJobsAdapter(jobList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jobAdapter
        }
    }

    private fun fetchJobs() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiClient = CandidateProfileRetrofitClient.instance(requireContext())
                    .create(CandidateInterface::class.java)
                val response = apiClient.getrecruiterpostedJobs()

                if (response.isSuccessful) {
                    val jobs = response.body() ?: emptyList()

                    withContext(Dispatchers.Main) {
                        jobList.clear()
                        jobList.addAll(jobs)
                        jobAdapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error fetching jobs", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
