package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentExperienceBinding
import com.google.android.material.textfield.TextInputLayout

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
            validateInputs()
        }

        return binding.root
    }

    private fun validateInputs() {
        var exp = binding.exp.editText?.text.toString().trim()
        var company_name  = binding.companyName.editText?.text.toString().trim()
        var position  = binding.position.editText?.text.toString().trim()
        var year_of_completion = binding.yearOfWork.editText?.text.toString().trim()

        var hasError = false

        if (exp.isBlank()) {
            binding.exp.error = "Field Empty"
            binding.exp.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.exp.clearFocus()
            hasError = true
        }

        if (company_name.isBlank()) {
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

        if (year_of_completion.isBlank()) {
            binding.yearOfWork.error = "Field Empty"
            binding.yearOfWork.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.yearOfWork.clearFocus()
            hasError = true
        }

        if (hasError) {
            binding.nextFragment.isEnabled = true
            return
        } else {
//            showLoadingDialog()
            //Api function call
            sharedViewModel.exp=exp
            sharedViewModel.company_name=company_name
            sharedViewModel.position=position
            sharedViewModel.year_of_Work=year_of_completion
            findNavController().navigate(R.id.certificates)
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