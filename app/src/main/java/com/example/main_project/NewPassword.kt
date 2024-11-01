package com.example.main_project

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentNewPasswordBinding

class NewPassword : Fragment() {

    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)

        binding.typePassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.typePassword.error = null
            }
        }

        binding.editPassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editPassword.error = null
            }
        }

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

    private fun validateInputs() {
        val email = binding.typePassword.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        if (email.isBlank()) {
            binding.typePassword.error = "*Required"
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
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
