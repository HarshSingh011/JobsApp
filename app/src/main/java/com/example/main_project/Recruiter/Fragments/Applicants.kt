package com.example.main_project.Recruiter.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.ApplicantsResponse
import com.example.main_project.adapters.RecruiterApplicantsAdapter
import com.example.main_project.databinding.FragmentApplicantsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class Applicants : Fragment() {

    private var _binding: FragmentApplicantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var applicantsAdapter: RecruiterApplicantsAdapter
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicantsBinding.inflate(inflater, container, false)

        setupRecyclerView()

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
        showLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiClient = CandidateProfileRetrofitClient.instance(requireContext())
                    .create(CandidateInterface::class.java)

                val response = apiClient.getJobApplications(jobId)

                if (response.isSuccessful) {
                    loadingDialog.dismiss()
                    val applicants = response.body() ?: emptyList()

                    withContext(Dispatchers.Main) {
                        applicantsAdapter = RecruiterApplicantsAdapter(applicants, requireContext())
                        binding.recyclerViewApplicants.adapter = applicantsAdapter
                    }
                } else {
                    loadingDialog.dismiss()
                    val errorResponse = response.errorBody()?.string()
                    println("failer")
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                e.printStackTrace()
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
    private fun parseErrorMessage(response: String?): String {
        return try {
            val jsonObject = JSONObject(response ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "An error occurred"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
