package com.example.main_project.SettingProfile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.databinding.FragmentRecuriterProfileBinding

class RecuriterProfile : Fragment() {

    private var _binding: FragmentRecuriterProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuriterProfileBinding.inflate(inflater, container, false)

        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.searchJob)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}