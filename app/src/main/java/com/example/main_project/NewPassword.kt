package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentForgotPasswordBinding
import com.example.main_project.databinding.FragmentNewPasswordBinding

class NewPassword : Fragment() {

    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.verificationCode)
        }

        return binding.root
    }
}