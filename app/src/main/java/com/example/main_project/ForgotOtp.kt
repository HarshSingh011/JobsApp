package com.example.main_project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentForgotOtpBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotOtp : Fragment() {

    private var _binding: FragmentForgotOtpBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotOtpBinding.inflate(inflater, container, false)

        setupUI()
        setUpOtpEditTexts()

        return binding.root
    }

    private fun setupUI() {
        binding.loginBtn.setOnClickListener {
            if (areAllDigitsEntered()) {
                val otp = getEnteredOtp()
                sendOtpToApi(
                    sharedViewModel.contact,
                    otp,
                    sharedViewModel.newPassword,
                    sharedViewModel.confirmPassword
                )
            } else {
                applyErrorToAllEditTexts()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })
    }

    private fun getEnteredOtp(): String {
        return with(binding) {
            digitOne.text.toString() +
                    digitTwo.text.toString() +
                    digitThree.text.toString() +
                    digitFour.text.toString() +
                    digitFive.text.toString() +
                    digitSix.text.toString()
        }
    }

    private fun sendOtpToApi(contact: String, otp: String, newPassword: String, confirmPassword: String) {
        val request = ChangePasswordRequest(contact, otp, newPassword, confirmPassword)

        RetrofitClient.instance.changepassword(request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    if (message == "Password changed successfully") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.verified)
                    } else {
                        applyErrorToAllEditTexts()
                        Toast.makeText(requireContext(), parseErrorMessage(response.errorBody()?.string()), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    applyErrorToAllEditTexts()
                    Toast.makeText(requireContext(), "Invalid OTP or an error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                applyErrorToAllEditTexts()
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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

    private fun setUpOtpEditTexts() {
        val editTexts = listOf(
            binding.digitOne,
            binding.digitTwo,
            binding.digitThree,
            binding.digitFour,
            binding.digitFive,
            binding.digitSix
        )

        for ((index, editText) in editTexts.withIndex()) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        if (s.isNotEmpty()) {
                            editText.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_prop)
                        }
                        if (s.length == 1 && index < editTexts.size - 1) {
                            editTexts[index + 1].requestFocus()
                        } else if (s.isEmpty() && index > 0) {
                            editTexts[index - 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && editText.text.isEmpty()) {
                    val previousIndex = editTexts.indexOf(editText) - 1
                    if (previousIndex >= 0) {
                        editTexts[previousIndex].requestFocus()
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun areAllDigitsEntered(): Boolean {
        return binding.digitOne.text.isNotEmpty() &&
                binding.digitTwo.text.isNotEmpty() &&
                binding.digitThree.text.isNotEmpty() &&
                binding.digitFour.text.isNotEmpty() &&
                binding.digitFive.text.isNotEmpty() &&
                binding.digitSix.text.isNotEmpty()
    }

    private fun applyErrorToAllEditTexts() {
        val editTexts = listOf(
            binding.digitOne,
            binding.digitTwo,
            binding.digitThree,
            binding.digitFour,
            binding.digitFive,
            binding.digitSix
        )

        for (editText in editTexts) {
            if (editText.text.isEmpty()) {
                editText.background = ContextCompat.getDrawable(requireContext(), R.drawable.error_prop)
            }
        }
    }

    private fun clearAllEntries() {
        binding.digitOne.text.clear()
        binding.digitTwo.text.clear()
        binding.digitThree.text.clear()
        binding.digitFour.text.clear()
        binding.digitFive.text.clear()
        binding.digitSix.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
