package com.example.main_project.Recruiter.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.main_project.SettingProfile.ViewModel.RecruiterViewModel
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.CandidateInterface
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.RecruiterData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateResponse
import com.example.main_project.databinding.FragmentRecruiterDetailsBinding
import com.example.main_project.databinding.FragmentRecruiterDetailsEditBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RecruiterDetailsEdit : Fragment() {

    private var _binding: FragmentRecruiterDetailsEditBinding? = null
    private val binding get() = _binding!!

    private val recruiterViewModel: RecruiterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecruiterDetailsEditBinding.inflate(inflater, container, false)

        recruiterViewModel.recruiterData.observe(viewLifecycleOwner, Observer { recruiterData ->
            binding.CompanyNameInputbox.setText(recruiterData.companyName)
            binding.CompanyEmailInputbox.setText(recruiterData.companyEmail)
            binding.JobTitleInputbox.setText(recruiterData.jobTitle)
            binding.CompanyWebsiteInputbox.setText(recruiterData.companyWebsite)
            binding.IndustryInputbox.setText(recruiterData.industry)
            binding.CompanyLocationInputbox.setText(recruiterData.companyLocation)
        })

        binding.submit.setOnClickListener {
            val companyName = binding.CompanyNameInputbox.text.toString()
            val companyEmail = binding.CompanyEmailInputbox.text.toString()
            val jobTitle = binding.JobTitleInputbox.text.toString()
            val companyWebsite = binding.CompanyWebsiteInputbox.text.toString()
            val industry = binding.IndustryInputbox.text.toString()
            val companyLocation = binding.CompanyLocationInputbox.text.toString()

            val updateData = RecruiterUpdateData(
                companyName = companyName,
                companyEmail = companyEmail,
                jobTitle = jobTitle,
                companyWebsite = companyWebsite,
                industry = industry,
                companyLocation = companyLocation
            )

            updateRecruiterData(requireContext(), updateData)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.recruiterDetails)
            }
        })

        return binding.root
    }

    private fun updateRecruiterData(context: Context, updateData: RecruiterUpdateData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = CandidateProfileRetrofitClient.instance(context).create(CandidateInterface::class.java)
                val response: Response<RecruiterUpdateResponse> = service.updateRecruiter(updateData)

                println(updateData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val message = response.body()?.message
                        Toast.makeText(context, message ?: "Recruiter updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Something went wrong"
                        val errorResponse = Gson().fromJson(errorMessage, RecruiterUpdateResponse::class.java)
                        Toast.makeText(context, errorResponse.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
