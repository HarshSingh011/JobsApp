package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentUsernameBinding

class Username : Fragment() {

    private var _binding: FragmentUsernameBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsernameBinding.inflate(inflater, container, false)

        binding.contactenter.setOnClickListener {
            validateAndNavigate()
        }

        setupTextWatchers()

        return binding.root
    }

    private fun setupTextWatchers() {
        binding.editFirstName.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editFirstName.error = null
            }
        }

        binding.editLastName.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editLastName.error = null
            }
        }
    }

    private fun validateAndNavigate() {
        val firstName = binding.editFirstName.editText?.text.toString() ?: ""
        val lastName = binding.editLastName.editText?.text.toString() ?: ""

        var hasError = false

        if (firstName.isBlank()) {
            binding.editFirstName.error = "*Required"
            hasError = true
        }

        if (lastName.isBlank()) {
            binding.editLastName.error = "*Required"
            hasError = true
        }

        if (!hasError) {
            sharedViewModel.firstName = firstName
            sharedViewModel.lastName = lastName
            findNavController().navigate(R.id.signUp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
