package com.example.main_project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentForgotOtpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Forgot_otp : Fragment() {

    private var _binding: FragmentForgotOtpBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotOtpBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            if (areAllDigitsEntered()) {
                val otp = getEnteredOtp()
                sendOtpToApi(sharedViewModel.contact, otp, sharedViewModel.newPassword, sharedViewModel.confirmPassword)
            } else {
                clearAllEntries()
                setEditTextErrorOutline()
                unfocusEditTexts()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        setUpOtpEditTexts()

        return binding.root
    }

    private fun getEnteredOtp(): String {
        return binding.digitOne.text.toString() +
                binding.digitTwo.text.toString() +
                binding.digitThree.text.toString() +
                binding.digitFour.text.toString() +
                binding.digitFive.text.toString() +
                binding.digitSix.text.toString()
    }

    private fun sendOtpToApi(contact: String, otp: String, newPassword: String, confirmPassword: String) {
        val request = ChangePasswordRequest(contact = contact, otp = otp, newPassword = newPassword, confirmPassword = confirmPassword)
        val call = RetrofitClient.instance.changepassword(request)

        call.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()?.message
                    if (message == "Password changed successfully") {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.verified) // Navigate to the next fragment
                    } else {
                        clearAllEntries()
                        Toast.makeText(context, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    clearAllEntries()
                    Toast.makeText(context, "Invalid OTP or error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                        if (s.length == 1 && index < editTexts.size - 1) {
                            editTexts[index + 1].requestFocus()
                        } else if (s.isEmpty() && index > 0) {
                            editTexts[index - 1].requestFocus()
                        }
                    }

                    if (s != null && s.isNotEmpty()) {
                        editText.background = resources.getDrawable(R.drawable.edittext_prop)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && (editText.text.isEmpty() && editText.isFocused)) {
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

    private fun setEditTextErrorOutline() {
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
                editText.background = resources.getDrawable(R.drawable.error_prop)
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

    private fun unfocusEditTexts() {
        val imm = activity?.getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.loginBtn.windowToken, 0)

        val editTexts = listOf(
            binding.digitOne,
            binding.digitTwo,
            binding.digitThree,
            binding.digitFour,
            binding.digitFive,
            binding.digitSix
        )

        for (editText in editTexts) {
            editText.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}