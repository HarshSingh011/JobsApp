package com.example.main_project.Auth.Fragments

import com.example.main_project.Auth.DataClasses.RegisterRequestPhone
import com.example.main_project.Auth.DataClasses.RegisterResponse
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.Auth.ViewModels.RegisterViewModel
import com.example.main_project.Auth.RetrofitClient
import com.example.main_project.databinding.FragmentSignupPhoneBinding
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupPhone : Fragment() {

    private var _binding: FragmentSignupPhoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingDialog: Dialog

    private val sharedViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupPhoneBinding.inflate(inflater, container, false)

        binding.getOTP.setOnClickListener {
            validateInputs()
        }

        binding.signin.setOnClickListener {
            findNavController().navigate(R.id.loginPage)
        }

        binding.editEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.editEmail)
            }
        }

        binding.editPassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetToDefaultDrawable(binding.editPassword)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        return binding.root
    }

    private fun validateInputs() {
        val input = binding.editEmail.editText?.text.toString().trim()
        val password = binding.editPassword.editText?.text.toString().trim()

        var hasError = false

        if (input.isBlank() || input.length != 10) {
            binding.editEmail.clearFocus()
            binding.editPassword.clearFocus()
            binding.editEmail.error = "10 digit mobile number required"
            binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editEmail.editText?.setBackgroundResource(R.drawable.edittext_prop)
        }

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(password)) {
            binding.editEmail.clearFocus()
            binding.editPassword.clearFocus()
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
        }

        if (password.isBlank()) {
            binding.editPassword.error = "Required"
            binding.editEmail.clearFocus()
            binding.editPassword.clearFocus()
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
        }

        if (!hasError) {
            val mobile = "+91$input"
            sharedViewModel.email = mobile
            binding.getOTP.isEnabled = false
            sendDataToApi(mobile, password)
            showLoadingDialog()
        }
    }

    private fun sendDataToApi(mobile: String, password: String) {
        binding.getOTP.isEnabled = false

        val firstName = sharedViewModel.firstName
        val lastName = sharedViewModel.lastName

        val request = RegisterRequestPhone(firstName, lastName, mobile, password)

        RetrofitClient.instance.registerPhone(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                binding.getOTP.isEnabled = true
                loadingDialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    val responseMessage = response.body()?.message ?: "Registration successful"
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.verificationCode)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    handleApiError(errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.getOTP.isEnabled = true
                loadingDialog.dismiss()

                val errorMessage = if (t is java.net.SocketTimeoutException) {
                    "Request timed out. Please try again."
                } else {
                    "Error: ${t.message}"
                }
                handleApiError(errorMessage)
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

    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.loader)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }
    private fun handleApiError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        binding.editEmail.error = errorMessage
        binding.editEmail.clearFocus()
        binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
    }

    private fun resetToDefaultDrawable(editTextLayout: TextInputLayout) {
        editTextLayout.error = null
        editTextLayout.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
