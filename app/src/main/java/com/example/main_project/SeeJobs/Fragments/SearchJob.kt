package com.example.main_project.SeeJobs.Fragments


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.databinding.FragmentSearchJobBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchJob : Fragment() {

    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchJobBinding.inflate(inflater, container, false)

        jobRecyclerView = binding.jobsearch
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        fetchJobs()

        binding.filter.setOnClickListener(){
            findNavController().navigate(R.id.jobFilter)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.candidateProfile)
                }
            }
        )

        return binding.root
    }

    private fun fetchJobs() {
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)
        showLoadingDialog()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<List<Job>> = apiService.getJobs()

                if (response.isSuccessful) {
                    loadingDialog.dismiss()
                    val jobList = response.body()

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
                    Log.e("API Error", "Error fetching jobs: ${response.code()}")
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                Log.e("API Error", "Error fetching jobs: ${e.message}")
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
            putString("job_salary", job.maxSalary.toString()+" - "+job.minSalary.toString())
            putString("job_location", job.location)
            putString("job_Mode", job.mode)
            putString("job_type", job.jobType)
        }
        findNavController().navigate(R.id.jobsDetails, bundle)
    }
}