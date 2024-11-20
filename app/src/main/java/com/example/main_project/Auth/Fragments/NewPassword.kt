package com.example.main_project.Auth.Fragments

import com.example.main_project.Auth.DataClasses.ChangePasswordResponse
import com.example.main_project.Auth.DataClasses.NewPasswordFormedRequest
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.Auth.ViewModels.ForgotPasswordViewModel
import com.example.main_project.R
import com.example.main_project.Auth.RetrofitClient
import com.example.main_project.databinding.FragmentNewPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPassword : Fragment() {

    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        binding.loginBtn.isEnabled = true

        binding.loginBtn.setOnClickListener {
            validateInputs()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        binding.typePassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetTypePasswordField()
            }
        }

        binding.editPassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetPasswordField()
            }
        }

        return binding.root
    }

    private fun validateInputs() {
        val type = binding.typePassword.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        var hasError = false

        if (type != password) {
            binding.editPassword.error = "Not matched"
            binding.editPassword.clearFocus()
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        if (!passwordRegex.matches(type) || !passwordRegex.matches(password)) {
            binding.editPassword.clearFocus()
            binding.editPassword.clearFocus()
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.typePassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (type.isBlank()) {
            binding.typePassword.error = "Required"
            binding.typePassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (password.isBlank()) {
            binding.editPassword.error = "Required"
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        }

        if (hasError) {
            binding.typePassword.editText?.clearFocus()
            binding.editPassword.editText?.clearFocus()
        } else {
            binding.loginBtn.isEnabled = false
            sharedViewModel.newPassword = type
            sharedViewModel.confirmPassword = password
            changePassword(sharedViewModel.contact,type,password)
            showLoadingDialog()
        }
    }

    private fun applyErrorBackground(editText1: TextInputLayout, editText2: TextInputLayout , errorMessage: String) {
//        editText1.editText?.setBackgroundResource(R.drawable.error_prop)
        editText2.editText?.setBackgroundResource(R.drawable.error_prop)
//        editText1.error = errorMessage
        editText2.error = errorMessage
        editText1.editText?.clearFocus()
        editText2.editText?.clearFocus()
    }

    private fun changePassword(contact: String, newPassword: String, confirmPassword: String) {
        val request = NewPasswordFormedRequest(contact,newPassword,confirmPassword)
        val call = RetrofitClient.instance.changePassword(request)
        Log.d("ChangePasswordRequest", "Request Body: $request")

        call.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()

                if (response.isSuccessful) {
                    response.body()?.let { newPasswordBody->
                        Toast.makeText(requireContext(), newPasswordBody.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.verified)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let { parseErrorMessage(it) } ?: "An error occurred"
                    applyErrorBackground(binding.typePassword, binding.editPassword, errorMessage)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("ForgotPasswordError", "Response code: ${response.code()} - $errorMessage")
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ForgotPasswordFailure", "Error: ${t.message}")
            }
        })
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

    private fun parseErrorMessage(response: String): String {
        return try {
            val jsonObject = JSONObject(response)
            jsonObject.getString("message")
        } catch (e: Exception) {
            Log.e("ParseError", "Failed to parse error message", e)
            "An error occurred"
        }
    }

    private fun resetTypePasswordField() {
        binding.typePassword.error = null
        binding.typePassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    private fun resetPasswordField() {
        binding.editPassword.error = null
        binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
