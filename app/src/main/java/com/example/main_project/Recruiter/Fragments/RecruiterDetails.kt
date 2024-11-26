package com.example.main_project.Recruiter.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.main_project.databinding.FragmentRecruiterDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RecruiterDetails : Fragment() {

    private var _binding: FragmentRecruiterDetailsBinding? = null
    private val binding get() = _binding!!

    private val recruiterViewModel: RecruiterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecruiterDetailsBinding.inflate(inflater, container, false)

        fetchRecruiterData(requireContext())

        recruiterViewModel.recruiterData.observe(viewLifecycleOwner, Observer { recruiterData ->
            binding.firstname.text = recruiterData.firstName ?: "N/A"
            binding.lastname.text = recruiterData.lastName ?: "N/A"
            binding.Email.text = recruiterData.email ?: "N/A"
            binding.CompanyName.text = recruiterData.companyName ?: "N/A"
            binding.phonenumber.text = recruiterData.phone ?: "N/A"
            binding.CompanyEmail.text = recruiterData.companyEmail ?: "N/A"
            binding.JobTitle.text = recruiterData.jobTitle ?: "N/A"
            binding.CompanyWebsite.text = recruiterData.companyWebsite ?: "N/A"
            binding.CompanyLocation.text = recruiterData.companyLocation ?: "N/A"
            binding.Industry.text = recruiterData.industry ?: "N/A"

            if (!recruiterData.profileImage.isNullOrEmpty()) {
                Glide.with(this)
                    .load(recruiterData.profileImage)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_launcher_background)
            }
        })

        binding.editData.setOnClickListener(){
            findNavController().navigate(R.id.recruiterDetailsEdit)
        }

        binding.PostJob.setOnClickListener(){
            findNavController().navigate(R.id.postAJob)
        }

        return binding.root
    }

    private fun fetchRecruiterData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = CandidateProfileRetrofitClient.instance(context).create(CandidateInterface::class.java)
                val response: Response<RecruiterData> = service.getCurrentRecruiter()

                if (response.isSuccessful) {
                    response.body()?.let {
                        recruiterViewModel.saveRecruiterData(it)
                    }
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
