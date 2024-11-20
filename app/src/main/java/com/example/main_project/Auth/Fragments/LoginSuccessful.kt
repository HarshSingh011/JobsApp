package com.example.main_project.Auth.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.DataStoreManager
import com.example.main_project.R
import kotlinx.coroutines.launch

class LoginSuccessful : Fragment() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_successfull, container, false)
        dataStoreManager = DataStoreManager(requireContext())

        val loginBtn: Button = view.findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.deleteToken()
                findNavController().navigate(R.id.loginPage)
            }
        }

        val nextbtn: Button = view.findViewById(R.id.next)
        nextbtn.setOnClickListener {
            findNavController().navigate(R.id.yourProfile)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        )

        return view
    }
}


