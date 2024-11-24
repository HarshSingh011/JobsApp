package com.example.main_project.SettingProfile.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.Educations
import com.example.main_project.TabAdapter
import com.example.main_project.databinding.FragmentYourProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout

class YourProfile : Fragment() {

    private var _binding: FragmentYourProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CandidateViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, role)
        binding.roleDefine.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourProfileBinding.inflate(inflater, container, false)

        binding.addEducation.setOnClickListener {
            addDataToCandidateData()
        }

        binding.searchBox.setEndIconOnClickListener {
            val enteredDomain = binding.autoCompleteDomain.text.toString().trim()
            if (enteredDomain.isNotEmpty()) {
                if (sharedViewModel.domain.isEmpty()) {
                    sharedViewModel.domain = enteredDomain
                } else {
                    sharedViewModel.domain += ", $enteredDomain"
                    println(sharedViewModel)
                }
                Toast.makeText(requireContext(), "Domain added: $enteredDomain", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a domain to add", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 1
            } else {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        val domainSuggestions = listOf(
            "Web Developer", "Front End", "Back End", "Data Scientist",
            "Software Engineer", "Android Developer", "Game Developer"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, domainSuggestions)
        binding.autoCompleteDomain.setAdapter(adapter)

        binding.role.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.role)
        }

        binding.instituteName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.instituteName)
        }

        binding.Degree.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.Degree)
        }

        binding.yearOfCompleletion.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.yearOfCompleletion)
        }

        binding.DateOfBirth.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.DateOfBirth)
        }

        binding.pic.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginSuccessful)
            }
        })

        return binding.root
    }

    private fun validateInputs(): Boolean {
        val companyName = binding.instituteName.editText?.text.toString().trim()
        val position = binding.Degree.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfCompleletion.editText?.text.toString().trim()
        val DOB = binding.DateOfBirth.editText?.text.toString().trim()

        var isValid = true

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.instituteName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.Degree)
            isValid = false
        }

        if (yearOfWork.isBlank()) {
            setToErrorDrawable(binding.yearOfCompleletion)
            isValid = false
        }

        if (DOB.isBlank()) {
            setToErrorDrawable(binding.DateOfBirth)
            isValid = false
        }

        return isValid
    }

    private fun addDataToCandidateData() {
        val institution = binding.instituteName.editText?.text.toString().trim()
        val degree = binding.Degree.editText?.text.toString().trim()
        val yearOfCompletion = binding.yearOfCompleletion.editText?.text.toString().trim().toIntOrNull()
        val DOB = binding.DateOfBirth.editText?.text.toString().trim()
        sharedViewModel.DOB = DOB


        if (institution.isNotEmpty() && degree.isNotEmpty() && yearOfCompletion != null) {
            val newEducation = Educations(institution, degree, yearOfCompletion)
            sharedViewModel.educationList.add(newEducation)
            Toast.makeText(requireContext(), "Education added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please fill all education details", Toast.LENGTH_SHORT).show()
            return
        }

        println(sharedViewModel.educationList)
        println(sharedViewModel.domain)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileUri = data?.data
        if (fileUri != null) {
            binding.pic.setImageURI(fileUri)
            binding.pic.setContentPadding(0, 0, 0, 0)
        } else {
            Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

