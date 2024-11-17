package com.example.main_project

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentLoginPageBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginPage : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        dataStoreManager = DataStoreManager(requireContext())

        binding.forgot.setOnClickListener {
            findNavController().navigate(R.id.forgotPassword)
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.username)
        }

        binding.loginBtn.setOnClickListener {
            if (isNetworkAvailable()) {
                binding.loginBtn.isEnabled = false
                validateInputs()
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
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


    private fun validateInputs() {
        var input = binding.editEmail.editText?.text.toString().trim()
        val password = binding.editPassword.editText?.text.toString().trim()

        var hasError = false

        if (input.isBlank()) {
            binding.editEmail.error = "Required"
            binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editEmail.clearFocus()
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
            binding.editEmail.clearFocus()
            binding.editPassword.error = "8-20 char, A-Z, a-z, 0-9, and symbol"
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            hasError = true
        } else {
            binding.editPassword.editText?.setBackgroundResource(R.drawable.edittext_prop)
        }

        if (hasError) {
            binding.loginBtn.isEnabled = true
            return
        } else {
            showLoadingDialog()
            loginUser(input, password)
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

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(contact = email, password = password)

        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val token = loginResponse.token
                        if (token != null) {
                            findNavController().navigate(R.id.loginSuccessful)
                            if (binding.chkbox.isChecked) {
                                lifecycleScope.launch {
                                    dataStoreManager.saveToken(token)
                                }
                            }
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
                println("fail")
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()

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

        if( message == "Incorrect password"){
            binding.editPassword.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editPassword.error = message
        }else{
            binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
            binding.editEmail.error = message
        }

        binding.editEmail.editText?.clearFocus()
        binding.editPassword.editText?.clearFocus()
    }

    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
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
