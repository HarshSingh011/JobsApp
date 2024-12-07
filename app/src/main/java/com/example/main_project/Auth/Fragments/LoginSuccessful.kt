package com.example.main_project.Auth.Fragments

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.DataStoreManager
import com.example.main_project.MainActivity3
import com.example.main_project.MainActivity5
import com.example.main_project.R
import com.example.main_project.SettingProfile.MainActivity2
import com.example.main_project.databinding.FragmentSuccessfullBinding
import kotlinx.coroutines.launch

class LoginSuccessful : Fragment() {

    private var _binding: FragmentSuccessfullBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("LoginSuccessful fragment loaded")
        _binding = FragmentSuccessfullBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())
        

        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, role)
        binding.roleDefine.setAdapter(arrayAdapter)

        binding.next.setOnClickListener {
            val selectedRole = binding.roleDefine.text.toString()
            println("Selected role: $selectedRole")
            when (selectedRole) {
                "Candidate" -> {
                    println("Navigating to mainActivity2")
                    val intent = Intent(requireContext(), MainActivity2::class.java)
                    startActivity(intent)
                }
                "Recruiter" -> {
                    println("Navigating to mainActivity3")
                    val intent = Intent(requireContext(), MainActivity3::class.java)
                    startActivity(intent)
                }
                else -> {
                    println("Staying in LoginSuccessful")
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.forgotOtp) {
                    findNavController().navigate(R.id.loginSuccessful)
                }
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
