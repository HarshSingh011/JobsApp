package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentSignupEmailBinding
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupEmail : Fragment() {

    private var _binding: FragmentSignupEmailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupEmailBinding.inflate(inflater, container, false)

        binding.getOTP.setOnClickListener {
            validateInputs()
        }

        setupTextWatchers()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.signin.setOnClickListener {
            findNavController().navigate(R.id.loginPage)
        }

        return binding.root
    }

    private fun setupTextWatchers() {
        binding.editEmail.editText?.doOnTextChanged { _, _, _, _ ->
            clearErrorBackground(binding.editEmail)
        }

        binding.editPassword.editText?.doOnTextChanged { _, _, _, _ ->
            clearErrorBackground(binding.editPassword)
        }
    }

    private fun validateInputs() {
        val email = binding.editEmail.editText?.text.toString().trim()
        val password = binding.editPassword.editText?.text.toString().trim()

        var hasError = false

        if (email.isBlank()) {
            applyErrorBackground(binding.editEmail, "Email is required")
            hasError = true
        }

        if (password.isBlank()) {
            applyErrorBackground(binding.editPassword, "Password is required")
            hasError = true
        }

        if (!hasError) {
            sharedViewModel.email = email
            sendDataToApi(email, password)
        }
    }

    private fun applyErrorBackground(editText: TextInputLayout, errorMessage: String) {
        editText.editText?.background = ResourcesCompat.getDrawable(
            resources, R.drawable.error_prop, null
        )
        editText.error = errorMessage
        editText.editText?.clearFocus()
    }

    private fun clearErrorBackground(editText: TextInputLayout) {
        editText.error = null
        editText.editText?.background = ResourcesCompat.getDrawable(
            resources, R.drawable.edittext_prop, null
        )
    }

    private fun sendDataToApi(email: String, password: String) {
        val firstName = sharedViewModel.firstName ?: ""
        val lastName = sharedViewModel.lastName ?: ""

        val request = RegisterRequestEmail(firstName, lastName, email, password)

        RetrofitClient.instance.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseMessage = response.body()?.message ?: "Registration successful"
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.verificationCode)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    applyErrorBackground(binding.editEmail, errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = if (t is java.net.SocketTimeoutException) {
                    "Request timed out. Please try again."
                } else {
                    "Error: ${t.message}"
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                applyErrorBackground(binding.editEmail, errorMessage)
            }
        })
    }

    private fun parseErrorMessage(response: String): String {
        return try {
            val jsonObject = JSONObject(response)
            jsonObject.getString("message")
        } catch (e: Exception) {
            "An error occurred"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
