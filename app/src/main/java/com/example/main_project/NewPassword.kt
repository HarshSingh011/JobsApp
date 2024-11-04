package com.example.main_project

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentNewPasswordBinding

class NewPassword : Fragment() {

    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)

        setupTextWatchers()

        binding.loginBtn.setOnClickListener {
            validateInputs()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        return binding.root
    }

    private fun setupTextWatchers() {
        binding.typePassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                resetTypePasswordField()
            }
        }

        binding.editPassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                resetPasswordField()
            }
        }
    }

    private fun validateInputs() {
        val type = binding.typePassword.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        if (type != password) {
            binding.typePassword.error = "Not matched"
            binding.editPassword.error = "Not matched"
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(type) || !passwordRegex.matches(password)) {
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.typePassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (type.isBlank()) {
            binding.typePassword.error = "*Required"
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (hasError) {
            binding.typePassword.editText?.clearFocus()
            binding.editPassword.editText?.clearFocus()
        } else {
            sharedViewModel.newPassword = type
            sharedViewModel.confirmPassword = password
            findNavController().navigate(R.id.forgotPassword)
        }
    }

    private fun resetTypePasswordField() {
        binding.typePassword.error = null
        binding.typePassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    private fun resetPasswordField() {
        binding.editPassword.error = null
        binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
