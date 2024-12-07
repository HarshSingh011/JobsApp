package com.example.main_project.SeeJobs.Fragments

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.DataStoreManager
import com.example.main_project.R
import com.example.main_project.SeeJobs.Adapter.FilterAdapter
import com.example.main_project.SeeJobs.DataClasses.FilterItem
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.databinding.FragmentJobFilterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class JobFilter : Fragment() {

    private var _binding: FragmentJobFilterBinding? = null
    private val binding get() = _binding!!

    private lateinit var FilteredJobAdapter: FilteredJobAdapter
    private lateinit var loadingDialog: Dialog
    private lateinit var filterAdapter: FilterAdapter

    private val filterItems = listOf(
        FilterItem(1, "Title"),
        FilterItem(2, "Location"),
        FilterItem(3, "Min Salary"),
        FilterItem(4, "Max Salary"),
        FilterItem(5, "Employment Type"),
        FilterItem(6, "Experience"),
        FilterItem(7, "Skills"),
        FilterItem(8, "Mode"),
        FilterItem(9, "Job Type")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobFilterBinding.inflate(inflater, container, false)

        setupFilterRecyclerView()
        binding.Jobfilter.layoutManager = LinearLayoutManager(requireContext())

        binding.applyFiltersButton.setOnClickListener {
            val filters = filterAdapter.getFilterValues()
            Log.d("JobFilter", "Filters before API call: $filters")

            if (filters.isNotEmpty()) {
                showLoadingDialog()
                fetchJobs(filters)
            } else {
                Log.e("JobFilter", "No filters applied")
                Toast.makeText(context, "Please apply at least one filter", Toast.LENGTH_SHORT).show()
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

    private fun setupFilterRecyclerView() {
        filterAdapter = FilterAdapter(filterItems)
        binding.filterInput.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.filterInput.adapter = filterAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.filter_item_spacing)
        binding.filterInput.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position != parent.adapter?.itemCount?.minus(1)) {
                    outRect.right = spacingInPixels
                }
            }
        })
    }

    private fun fetchJobs(filters: Map<String, String>) {
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = DataStoreManager(requireContext()).getToken().first()
                Log.d("JobFilter", "Token before API call: $token")

                Log.d("JobFilter", "Sending filters to API: $filters")
                val response: Response<List<Job>> = apiService.getJobsWithFilters(filters)

                if (response.isSuccessful) {
                    loadingDialog.dismiss()
                    val jobList = response.body()
                    jobList?.let {
                        FilteredJobAdapter = FilteredJobAdapter(it.toMutableList()) { job ->
                            navigateToJobDetails(job)
                        }

                        binding.Jobfilter.adapter = FilteredJobAdapter
                        Log.d("JobFilter", "Jobs fetched successfully: ${it.size} jobs")
                    }
                } else {
                    loadingDialog.dismiss()
                    FilteredJobAdapter = FilteredJobAdapter(mutableListOf()) { job ->
                        navigateToJobDetails(job)
                    }
                    val errorResponse = response.errorBody()?.string()
                    println("failer")
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    binding.Jobfilter.adapter = FilteredJobAdapter
//                    Toast.makeText(context, "Error fetching jobs: ${response.code()}", Toast.LENGTH_SHORT).show()

                    if (response.code() == 403) {
                        Log.e("API Error", "403 Forbidden - Check authentication token")
                        Toast.makeText(context, "Authentication error. Please log in again.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                FilteredJobAdapter = FilteredJobAdapter(mutableListOf()) { job ->
                    navigateToJobDetails(job)
                }
                binding.Jobfilter.adapter = FilteredJobAdapter
                Log.e("API Error", "Error fetching jobs: ${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseErrorMessage(response: String?): String {
        return try {
            val jsonObject = JSONObject(response ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "An error occurred"
        }
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.loader)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingDialog.setCancelable(false)
        }
        loadingDialog.show()
    }

    private fun navigateToJobDetails(job: Job) {
        val bundle = Bundle().apply {
            putString("job_id", job.id.toString())
            putString("job_title", job.title)
            putString("job_description", job.description)
            putString("job_salary", "${job.minSalary} - ${job.maxSalary}")
            putString("job_location", job.location)
            putString("job_Mode", job.mode)
            putString("job_type", job.jobType)
            putString("job_profile_image", job.postedBy.profileImage)

        }
        findNavController().navigate(R.id.jobsDetails, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}

