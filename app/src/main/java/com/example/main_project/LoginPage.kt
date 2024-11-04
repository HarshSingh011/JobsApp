package com.example.main_project

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentLoginPageBinding
import android.widget.Toast
import org.json.JSONObject
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
            findNavController().navigate(R.id.newPassword)
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.username)
        }

        binding.loginBtn.setOnClickListener {
            if (isNetworkAvailable()) {
                validateInputs()
                binding.progressBar.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editEmail.editText?.addTextChangedListener(resetErrorTextWatcher)
        binding.editPassword.editText?.addTextChangedListener(resetErrorTextWatcher)

        binding.editEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable()
            }
        }

        binding.editPassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        )

        return binding.root
    }

    private val resetErrorTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            resetToDefaultDrawable()
        }
    }

    private fun validateInputs() {
        var input = binding.editEmail.editText?.text.toString().trim()
        val password = binding.editPassword.editText?.text.toString().trim()

        var hasError = false

        if (input.isBlank()) {
            binding.editEmail.error = "*Required"
            binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editEmail.editText?.setBackgroundResource(R.drawable.edittext_prop)
            val phoneRegex = "^[0-9]{10}$".toRegex()
            if (phoneRegex.matches(input)) {
                input = "+91$input"
            }
        }

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(password)) {
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
        }

        if (hasError) {
            binding.progressBar.visibility = View.GONE
            return
        }

        loginUser(input, password)
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(contact = email, password = password)

        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val token = loginResponse.token
                        if (token != null) {
                            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_token", token).apply()

                            findNavController().navigate(R.id.loginSuccessful)
                        } else {
                            val errorMessage = parseErrorMessage(response.errorBody()?.string())
                            showError(errorMessage)
                        }
                    } ?: showError("Unexpected response structure")
                } else {
                    val errorMessage = parseErrorMessage(response.errorBody()?.string())
                    showError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                val errorMessage = if (t is IOException) {
                    if (t.message?.contains("timeout") == true) {
                        "Network timeout. Please try again."
                    } else {
                        "Network error. Please check your connection."
                    }
                } else {
                    "Unexpected error: ${t.message}"
                }
                showError(errorMessage)
            }
        })
    }

    private fun parseErrorMessage(response: String?): String {
        return try {
            val jsonObject = JSONObject(response ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "An error occurred"
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

        binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
        binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)

        binding.editEmail.editText?.clearFocus()
        binding.editPassword.editText?.clearFocus()
    }

    private fun resetToDefaultDrawable() {
        binding.editEmail.editText?.setBackgroundResource(R.drawable.edittext_prop)
        binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
