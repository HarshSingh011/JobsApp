package com.example.main_project.Auth.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.Auth.ViewModels.RegisterViewModel
import com.example.main_project.R
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        binding.signup.setOnClickListener(){
            findNavController().navigate(R.id.loginPage)
        }

        binding.editFirstName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetFirstNameField()
            }
        }

        binding.contactenter.setOnClickListener {
            validateAndNavigate()
        }

        return binding.root
    }

    private fun validateAndNavigate() {
        val firstName = binding.editFirstName.editText?.text.toString()
        val lastName = binding.editLastName.editText?.text.toString()

        var hasError = false

        if (firstName.isBlank()) {
            binding.editFirstName.error = "Required"
            binding.editFirstName.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (hasError) {
            binding.editFirstName.editText?.clearFocus()
            binding.editLastName.editText?.clearFocus()
        } else {
            sharedViewModel.firstName = firstName
            sharedViewModel.lastName = lastName
            findNavController().navigate(R.id.signUp)
        }
    }

    private fun resetFirstNameField() {
        binding.editFirstName.error = null
        binding.editFirstName.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
