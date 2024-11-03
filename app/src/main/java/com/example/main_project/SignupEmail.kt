package com.example.main_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentSignupEmailBinding
import androidx.core.widget.doOnTextChanged
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
                findNavController().navigate(R.id.loginPage)
            }
        })

        return binding.root
    }
    private fun setupTextWatchers() {
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
    }

    private fun validateInputs() {

        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        if (email.isBlank()) {
            binding.editEmail.error = "*Required"
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "*Required"
            hasError = true
        }


        if (!hasError) {
            sharedViewModel.email = email
            sendDataToApi(email, password)
        }
    }
    private fun sendDataToApi(email: String, password: String) {
        val firstName = sharedViewModel.firstName
        val lastName = sharedViewModel.lastName

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
                    binding.editEmail.error = errorMessage
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = if (t is java.net.SocketTimeoutException) {
                    "Request timed out. Please try again."
                } else {
                    "Error: ${t.message}"
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                binding.editEmail.error = errorMessage
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
