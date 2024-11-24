package com.example.main_project.Auth.Fragments

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
import com.example.main_project.R
import com.example.main_project.databinding.FragmentSuccessfullBinding
import kotlinx.coroutines.launch

class LoginSuccessful : Fragment() {

    private var _binding: FragmentSuccessfullBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager

    override fun onResume() {
        super.onResume()
        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, role)
        binding.roleDefine.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessfullBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())

        // Logout Button Logic
        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.deleteToken()
                findNavController().navigate(R.id.loginPage)
            }
        }

        binding.next.setOnClickListener {
            val selectedRole = binding.roleDefine.text.toString()
            when (selectedRole) {
                "Candidate" -> findNavController().navigate(R.id.mainActivity2)
                "Recruiter" -> findNavController().navigate(R.id.mainActivity3)
                else -> {
                    binding.roleDefine.error = "Please select a valid role"
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
