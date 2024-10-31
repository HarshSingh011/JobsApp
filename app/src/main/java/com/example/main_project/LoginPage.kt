package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentLoginPageBinding
import androidx.core.widget.doOnTextChanged

class LoginPage : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        binding.forgot.setOnClickListener {
            findNavController().navigate(R.id.forgotPassword)
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.signUp)
        }

        binding.loginBtn.setOnClickListener {
            validateInputs()
        }

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        return binding.root
    }

    private fun validateInputs() {
        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        if (email.isBlank()) {
            binding.editEmail.error = "*Required"
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
            hasError = true
        }

        if (!hasError) {
            findNavController().navigate(R.id.loginSuccessful)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
