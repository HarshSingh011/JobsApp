package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentSignupEmailBinding
import androidx.core.widget.doOnTextChanged

class SignupEmail : Fragment() {
    private var _binding: FragmentSignupEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupEmailBinding.inflate(inflater, container, false)

        binding.getOTP.setOnClickListener {
            validateInputs()
        }

        setupTextWatchers()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })
        return binding.root
    }

    private fun setupTextWatchers() {
        binding.editEmail.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editEmail.error = null
            }
        }

        binding.editPassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editPassword.error = null
            }
        }

        binding.editName.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editName.error = null
            }
        }
    }

    private fun validateInputs() {
        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()
        val name = binding.editName.editText?.text.toString()

        var hasError = false

        if (email.isBlank()) {
            binding.editEmail.error = "*Required"
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
            hasError = true
        }

        if (name.isBlank()) {
            binding.editName.error = "*Required"
            hasError = true
        }

        if (!hasError) {
            findNavController().navigate(R.id.verificationCode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
