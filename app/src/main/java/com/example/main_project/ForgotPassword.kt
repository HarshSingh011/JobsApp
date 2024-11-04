package com.example.main_project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import com.example.main_project.databinding.FragmentForgotPasswordBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.cnt.setOnClickListener {
            validateInput()
        }

        binding.editEmail.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editEmail.error = null
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginPage)
                }
            })
    }

    private fun validateInput() {
        val input = binding.editEmail.editText?.text.toString().trim()

        if (input.isBlank()) {
            binding.editEmail.error = "*Required"
        } else {
            val username = if (input.matches(Regex("^[0-9]{10}\$"))) {
                "+91$input" // Add country code if input is a valid phone number
            } else {
                input
            }

            sharedViewModel.contact = username
            sendForgotPasswordRequest(username)
        }
    }

    private fun sendForgotPasswordRequest(username: String) {
        val request = ForgotPasswordRequest(contact = username)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { forgotPasswordResponse ->
                        Toast.makeText(requireContext(), forgotPasswordResponse.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.newPassword)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorResponse ?: "Unknown error")
                    binding.editEmail.error = errorMessage
                    Log.e("ForgotPasswordError", "Response code: ${response.code()} - $errorMessage")
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                val errorMessage = t.message ?: "Unknown error"
                Toast.makeText(requireContext(), "Request failed: $errorMessage", Toast.LENGTH_SHORT).show()
                Log.e("ForgotPasswordFailure", "Error: $errorMessage")
            }
        })
    }

    private fun parseErrorMessage(response: String): String {
        return try {
            val jsonObject = JSONObject(response)
            jsonObject.getString("message")
        } catch (e: Exception) {
            Log.e("ParseError", "Failed to parse error message", e)
            "An error occurred"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
