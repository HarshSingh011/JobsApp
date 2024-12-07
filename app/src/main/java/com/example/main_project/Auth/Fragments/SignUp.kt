package com.example.main_project.Auth.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.databinding.FragmentSignUpBinding

class SignUp : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var selectedOption: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        setupClickListeners()
        setupBackPressHandler()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.email.setOnClickListener {
            updateSelection("email")
        }

        binding.phone.setOnClickListener {
            updateSelection("phone")
        }

        binding.loginBtn.setOnClickListener {
            when (selectedOption) {
                "email" -> findNavController().navigate(R.id.signupEmail)
                "phone" -> findNavController().navigate(R.id.signupPhone)
                else -> Toast.makeText(context, "Please select a sign-up method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelection(option: String) {
        val selectedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.radio_btn_selected)
        val defaultDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.radio_bg)

        binding.email.background = if (option == "email") selectedDrawable else defaultDrawable
        binding.phone.background = if (option == "phone") selectedDrawable else defaultDrawable

        selectedOption = option
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

