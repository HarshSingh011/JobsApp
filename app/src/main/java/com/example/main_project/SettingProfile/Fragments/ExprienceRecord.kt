package com.example.main_project.SettingProfile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.main_project.R
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.SettingProfile.DataClasses.CandidateData
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
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 0
            }
        })

        binding.exp.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.exp)
        }

        binding.companyName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.companyName)
        }

        binding.position.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.position)
        }

        binding.yearOfWork.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.yearOfWork)
        }

        binding.submitButton.setOnClickListener {
            addExperienceDataToList()
        }

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                sendCandidateData()
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 2
            } else {
                Toast.makeText(requireContext(), "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun validateInputs(): Boolean {
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.position.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfWork.editText?.text.toString().trim()

        var isValid = true

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.companyName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.position)
            isValid = false
        }

        if (yearOfWork.isBlank()) {
            setToErrorDrawable(binding.yearOfWork)
            isValid = false
        }

        return isValid
    }

    private fun addExperienceDataToList() {
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.position.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfWork.editText?.text.toString().trim()


        Toast.makeText(requireContext(), "Experience added", Toast.LENGTH_SHORT).show()
        val experience = Experience(companyName, yearOfWork.toInt(), position)

        sharedViewModel.experienceList.add(experience)

        println(sharedViewModel.educationList)
        println(sharedViewModel.experienceList)
        println(sharedViewModel.domain)
    }

    private fun sendCandidateData() {
        lifecycleScope.launch {
            try {
                val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
                val api = retrofit.create(CandidateInterface::class.java)

                val candidateData = CandidateData(
                    educations = sharedViewModel.educationList,
                    experiences = sharedViewModel.experienceList,
                    skill = listOf(sharedViewModel.domain),
                    DOB = sharedViewModel.DOB
                )

                println(candidateData)

                val response = api.createCandidate(candidateData)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                    sharedViewModel.isApiSuccess = true
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    println(errorMessage)
                    Toast.makeText(context, "Submission failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println(e.message);
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
    }

    private fun setToErrorDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.error_prop)
        editText.error = "Empty Field!"
        editText.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
