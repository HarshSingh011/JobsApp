package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentForgotPasswordBinding
import com.example.main_project.databinding.FragmentLoginPageBinding

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        binding.cnt.setOnClickListener {
            findNavController().navigate(R.id.newPassword)
        }

        return binding.root
    }
}