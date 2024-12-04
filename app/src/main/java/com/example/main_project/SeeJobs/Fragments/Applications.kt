package com.example.main_project.SeeJobs.Fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SeeJobs.Adapter.ApplicationsAdapter
import com.example.main_project.SeeJobs.Adapters.JobApplicationAdapter
import com.example.main_project.SeeJobs.DataClasses.JobApplication
import com.example.main_project.databinding.FragmentApplicantsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class Applications : Fragment() {

    private var _binding: FragmentApplicantsBinding? = null
    private val binding get() = _binding!!
    private lateinit var applicationsRecyclerView: RecyclerView
    private lateinit var applicationsAdapter: ApplicationsAdapter
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicantsBinding.inflate(inflater, container, false)

        applicationsRecyclerView = binding.recyclerViewApplicants
        applicationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchapplicatioins()

        return binding.root
    }

    private fun fetchapplicatioins() {
        val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
        val apiService = retrofit.create(CandidateInterface::class.java)
        showLoadingDialog()

        CoroutineScope(Dispatchers.Main).launch {
            try {

                val jobApplicationResponse: Response<List<JobApplication>> = apiService.getJobApplications()

                if (jobApplicationResponse.isSuccessful) {
                    val jobApplicationList = jobApplicationResponse.body()
                    loadingDialog.dismiss()

                    jobApplicationList?.let {
                        if (::applicationsAdapter.isInitialized) {
                            applicationsAdapter.addApplications(it)
                        } else {
                            applicationsAdapter = ApplicationsAdapter(it.toMutableList())
                            applicationsRecyclerView.adapter = applicationsAdapter
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                    val errorResponse = jobApplicationResponse.errorBody()?.string()
                    println("failer")
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                loadingDialog.dismiss()
                Log.e("API Error", "Error fetching data: ${e.message}")
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
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
            loadingDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}