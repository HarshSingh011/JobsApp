package com.example.main_project.Auth.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.DataStoreManager
import com.example.main_project.R
import com.example.main_project.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.launch

class SplashScreen : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)

        dataStoreManager = DataStoreManager(requireContext())

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                dataStoreManager.getToken().collect { token ->
                    if (token != null) {
                        println("token is:")
                        println(token)
                        findNavController().navigate(R.id.loginSuccessful)
                    } else {
                        findNavController().navigate(R.id.frontPage)
                    }
                }
            }
        }, 2000)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
