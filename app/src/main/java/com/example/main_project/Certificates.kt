package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentCertificatesBinding
import com.example.main_project.databinding.FragmentExperienceBinding

class Certificates : Fragment() {

    private var _binding: FragmentCertificatesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCertificatesBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
            }
        })

        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.recuriterProfile1)
        }

        return binding.root
    }
}