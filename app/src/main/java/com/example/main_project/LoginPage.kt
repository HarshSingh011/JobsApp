package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentLoginPageBinding
import androidx.core.widget.doOnTextChanged
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        binding.forgot.setOnClickListener {
            findNavController().navigate(R.id.forgotPassword)
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.signUp)
        }

        binding.loginBtn.setOnClickListener {
            validateInputs()
        }

        binding.editEmail.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editEmail.error = null
            }
        }

        binding.editPassword.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editPassword.error = null
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        return binding.root
    }

    private fun validateInputs() {
        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(password)) {
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            hasError = true
        }

        if (email.isBlank()) {
            binding.editEmail.error = "*Required"
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
            hasError = true
        }

        if (!hasError) {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(username = email, password = password)

        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val token = loginResponse.token // Assuming `token` is the field name in `LoginResponse`

                    if (token != null) {
                        Toast.makeText(requireContext(), loginResponse.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.loginSuccessful)
                    } else {
                        binding.editEmail.error = "Invalid credentials"
                        binding.editPassword.error = "Invalid credentials"
                    }
                } else {
                    binding.editEmail.error = "Invalid credentials"
                    binding.editPassword.error = "Invalid credentials"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}