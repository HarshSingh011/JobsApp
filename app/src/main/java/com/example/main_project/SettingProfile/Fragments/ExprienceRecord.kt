package com.example.main_project.SettingProfile.Fragments

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.DataStoreManager
import com.example.main_project.R
import com.example.main_project.SettingProfile.CandidateInterface
import com.example.main_project.SettingProfile.CandidateProfileRetrofitClient
import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.SettingProfile.DataClasses.Education
import com.example.main_project.SettingProfile.DataClasses.Experience
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.databinding.FragmentExperienceBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ExprienceRecord : Fragment() {

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CandidateViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        val exp = resources.getStringArray(R.array.expri)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, exp)
        binding.expDefine.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExperienceBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
            }
        })

        binding.exp.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.exp)
            }
        }

        binding.companyName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.companyName)
            }
        }

        binding.position.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.position)
            }
        }

        binding.yearOfWork.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.yearOfWork)
            }
        }

        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.certificates)
//            validateInputs()
        }

        return binding.root
    }

    private fun validateInputs() {
        val exp = binding.exp.editText?.text.toString().trim()
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.position.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfWork.editText?.text.toString().trim()

        var hasError = false

        if (exp.isBlank()) {
            binding.exp.error = "Field Empty"
            binding.exp.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.exp.clearFocus()
            hasError = true
        }

        if (companyName.isBlank()) {
            binding.companyName.error = "Field Empty"
            binding.companyName.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.companyName.clearFocus()
            hasError = true
        }

        if (position.isBlank()) {
            binding.position.error = "Field Empty"
            binding.position.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.position.clearFocus()
            hasError = true
        }

        if (yearOfWork.isBlank()) {
            binding.yearOfWork.error = "Field Empty"
            binding.yearOfWork.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.yearOfWork.clearFocus()
            hasError = true
        }

        if (!hasError) {
            sendCandidateData(companyName, position, yearOfWork)
        }
    }

    private fun sendCandidateData(
        companyName: String,
        position: String,
        yearOfWork: String
    ) {
        lifecycleScope.launch {
            try {
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)

                val educationList = listOf(
                    Education(
                        institution = sharedViewModel.name_intitute,
                        degree = sharedViewModel.degree,
                        yearOfCompletion = sharedViewModel.year_of_completion.toInt()
                    )
                )

                val experienceList = listOf(
                    Experience(
                        companyName = companyName,
                        yearsWorked = yearOfWork.toInt(),
                        position = position
                    )
                )

                val candidateData = CandidateData(
                    education = educationList,
                    experiences = experienceList,
                    skill = sharedViewModel.domain
                )

                val response = api.createCandidate(candidateData)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.certificates)
                } else {
                    println(response.errorBody()?.string())
                    Toast.makeText(context, "Submission failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println(e.message)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
