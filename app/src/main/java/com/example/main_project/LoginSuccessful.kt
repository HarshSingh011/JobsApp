package com.example.main_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

        return view
    }
}


