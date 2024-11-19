package com.example.main_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentYourProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
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

        binding.nextFragment.setOnClickListener {
            validateInputs()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginSuccessful)
            }
        })

        binding.pic.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.role.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.role)
            }
        }

        binding.instituteName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.instituteName)
            }
        }

        binding.Degree.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.Degree)
            }
        }

        binding.yearOfCompleletion.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.yearOfCompleletion)
            }
        }

        binding.searchBox.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.searchBox)
            }
        }

        return binding.root
    }

    private fun validateInputs() {
        var role = binding.role.editText?.text.toString().trim()
        var name_intitute  = binding.instituteName.editText?.text.toString().trim()
        var degree  = binding.Degree.editText?.text.toString().trim()
        var year_of_completion = binding.yearOfCompleletion.editText?.text.toString().trim()
        var domain = binding.searchBox.editText?.text.toString().trim()

        var hasError = false

        if (role.isBlank()) {
            binding.role.error = "Field Empty"
            binding.role.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.role.clearFocus()
            hasError = true
        }

        if (name_intitute.isBlank()) {
            binding.instituteName.error = "Field Empty"
            binding.instituteName.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.role.clearFocus()
            hasError = true
        }

        if (degree.isBlank()) {
            binding.Degree.error = "Field Empty"
            binding.Degree.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.role.clearFocus()
            hasError = true
        }

        if (year_of_completion.isBlank()) {
            binding.yearOfCompleletion.error = "Field Empty"
            binding.yearOfCompleletion.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.role.clearFocus()
            hasError = true
        }

        if (domain.isBlank()) {
            binding.searchBox.error = "Field Empty"
            binding.searchBox.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.role.clearFocus()
            hasError = true
        }

        if (hasError) {
            binding.nextFragment.isEnabled = true
            return
        } else {
//            showLoadingDialog()
//            loginUser(input, password)
            sharedViewModel.role=role
            sharedViewModel.name_intitute= name_intitute
            sharedViewModel.degree=degree
            sharedViewModel.year_of_completion=year_of_completion
            sharedViewModel.domain=domain
            findNavController().navigate(R.id.experience)
        }
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

    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
