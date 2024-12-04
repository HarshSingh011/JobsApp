package com.example.main_project.SeeJobs.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SeeJobs.Adapters.JobApplicationAdapter
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.SeeJobs.DataClasses.JobApplication
import com.example.main_project.databinding.FragmentSearchJobBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchJob : Fragment() {

    private lateinit var jobSearchAdapter: SearchJobAdapter
    private lateinit var jobSearchRecyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobApplicationAdapter: JobApplicationAdapter
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var jobApplicationRecyclerView: RecyclerView
    private lateinit var loadingDialog: Dialog
    private lateinit var binding: FragmentSearchJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchJobBinding.inflate(inflater, container, false)
        jobRecyclerView = binding.jobsearch
        jobApplicationRecyclerView = binding.Appliedjobsearch

        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        jobApplicationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        jobSearchRecyclerView = binding.searchBoxRecyclerView
        jobSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        jobSearchAdapter = SearchJobAdapter(mutableListOf()) { job -> navigateToJobDetails(job) }
        jobSearchRecyclerView.adapter = jobSearchAdapter

        fetchJobs()

        binding.filter.setOnClickListener {
            findNavController().navigate(R.id.seeAllJobs)
        }

        binding.notification.setOnClickListener {
            findNavController().navigate(R.id.notification)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.mainActivity2)
                }
            }
        )

        val autoCompleteTextView = binding.searchBox
        autoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val title = autoCompleteTextView.text.toString().trim()
                if (title.isNotEmpty()) {
                    searchJobs(title)
                }
                true
            } else {
                false
            }
        }

        return binding.root
    }

    private fun searchJobs(title: String) {
        val jobApiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<List<Job>> = jobApiService.searchJobs(title)
                if (response.isSuccessful) {
                    val jobList = response.body() ?: emptyList()
                    jobSearchAdapter.updateJobs(jobList)

                    val elementCount = jobSearchAdapter.getSearchJobElementsCount()
                    val recyclerViewHeight = when {
                        elementCount == 1 -> 300
                        elementCount == 2 -> 600
                        elementCount > 2 -> 800
                        else -> 0
                    }

                    updateRecyclerViewHeight(recyclerViewHeight)

                    binding.searchBoxRecyclerView.visibility = if (jobList.isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRecyclerViewHeight(height: Int) {
        val layoutParams = binding.searchBoxRecyclerView.layoutParams
        layoutParams.height = height.dpToPx(requireContext())
        binding.searchBoxRecyclerView.layoutParams = layoutParams
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun fetchJobs() {
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)
        showLoadingDialog()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val jobResponse: Response<List<Job>> = apiService.getJobs()

                if (jobResponse.isSuccessful) {
                    loadingDialog.dismiss()
                    val jobList = jobResponse.body()

                    jobList?.let {
                        if (::jobAdapter.isInitialized) {
                            jobAdapter.addJobs(it)
                        } else {
                            jobAdapter = JobAdapter(it.toMutableList()) { job ->
                                navigateToJobDetails(job)
                            }
                            jobRecyclerView.adapter = jobAdapter
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                    Log.e("API Error", "Error fetching jobs: ${jobResponse.code()}")
                }

                val jobApplicationResponse: Response<List<JobApplication>> = apiService.getJobApplications()

                if (jobApplicationResponse.isSuccessful) {
                    val jobApplicationList = jobApplicationResponse.body()

                    jobApplicationList?.let {
                        if (::jobApplicationAdapter.isInitialized) {
                            jobApplicationAdapter.addApplications(it)
                        } else {
                            jobApplicationAdapter = JobApplicationAdapter(it.toMutableList())
                            jobApplicationRecyclerView.adapter = jobApplicationAdapter
                        }
                    }
                }

            } catch (e: Exception) {
                loadingDialog.dismiss()
                Log.e("API Error", "Error fetching data: ${e.message}")
            }
        }
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.loader)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
            loadingDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun navigateToJobDetails(job: Job) {
        val bundle = Bundle().apply {
            putString("job_id", job.id.toString())
            putString("job_title", job.title)
            putString("job_description", job.description)
            putString("job_salary", "${job.maxSalary} - ${job.minSalary}")
            putString("job_location", job.location)
            putString("job_Mode", job.mode)
            putString("job_type", job.jobType)
        }
        findNavController().navigate(R.id.jobsDetails, bundle)
    }

//    private fun navigateToJobDetails(job: Job) {
//        val bundle = Bundle().apply {
//            putString("job_id", job.id.toString())
//            putString("job_title", job.title)
//            putString("job_description", job.description)
//            putString("job_salary", job.maxSalary.toString() + " - " + job.minSalary.toString())
//            putString("job_location", job.location)
//            putString("job_Mode", job.mode)
//            putString("job_type", job.jobType)
//        }
//        findNavController().navigate(R.id.jobsDetails, bundle)
//    }

}
