package com.example.main_project

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
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
import java.io.IOException

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
            findNavController().navigate(R.id.username)
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })

        return binding.root
    }

    private fun validateInputs() {
        var input = binding.editEmail.editText?.text.toString().trim()
        val password = binding.editPassword.editText?.text.toString().trim()

        var hasError = false

        if (input.isBlank()) {
            binding.editEmail.error = "*Required"
            hasError = true
        } else {
            val phoneRegex = "^[0-9]{10}$".toRegex()
            if (phoneRegex.matches(input)) {
                input = "+91$input"
            }
        }

        val passwordRegex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(password)) {
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            hasError = true
        }

        if (hasError) return

        loginUser(input, password)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(contact = email, password = password)

        if (!isNetworkAvailable()) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE // Hide loading indicator

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val token = loginResponse.token

                        if (token != null) {
                            // Store the token in SharedPreferences
                            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_token", token).apply()

                            Toast.makeText(
                                requireContext(),
                                loginResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.loginSuccessful)
                        } else {
                            showInvalidCredentialsError()
                        }
                    } ?: showInvalidCredentialsError()
                } else {
                    showInvalidCredentialsError()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("LoginError", "Error during login", t)

                if (t is IOException) {
                    if (t.message?.contains("timeout") == true) {
                        Toast.makeText(
                            requireContext(),
                            "Network timeout. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unexpected error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun showInvalidCredentialsError() {
        binding.editEmail.error = "Invalid credentials"
        binding.editPassword.error = "Invalid credentials"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
