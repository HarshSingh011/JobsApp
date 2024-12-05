package com.example.main_project.candidate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.databinding.FragmentButtonPostAJobBinding
import com.example.main_project.databinding.FragmentCandidateProfileBinding

class ButtonPostAJob : Fragment() {

    private var _binding: FragmentButtonPostAJobBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentButtonPostAJobBinding.inflate(inflater, container, false)

        binding.textButton.setOnClickListener{
            findNavController().navigate(R.id.postAJob)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}