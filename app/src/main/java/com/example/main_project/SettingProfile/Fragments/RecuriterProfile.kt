package com.example.main_project.SettingProfile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.RequiterRequest
import com.example.main_project.databinding.FragmentRecuriterProfileBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RecuriterProfile : Fragment() {

    private var _binding: FragmentRecuriterProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuriterProfileBinding.inflate(inflater, container, false)

        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.recruiterDetails)
//            sendRecruiterData()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginSuccessful)
            }
        })

        return binding.root
    }

    private fun sendRecruiterData() {
        val recruiterRequest = RequiterRequest(
            companyName = binding.companyNameinput.text.toString().trim(),
            companyEmail = binding.companyEmailInput.text.toString().trim(),
            jobTitle = binding.JobTitleInput.text.toString().trim(),
            companyWebsite = binding.companyWebsiteinput.text.toString().trim(),
            industry = binding.industryInput.text.toString()
        )

        println(recruiterRequest)

        lifecycleScope.launch {
            try {
                println(recruiterRequest)
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)
                val response = api.createRecruiter(recruiterRequest)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Success: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.recruiterDetails)
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "HTTP Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unexpected Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
