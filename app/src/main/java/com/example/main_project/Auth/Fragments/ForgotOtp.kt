package com.example.main_project.Auth.Fragments

import com.example.main_project.Auth.DataClasses.ChangePasswordRequest
import com.example.main_project.Auth.DataClasses.ChangePasswordResponse
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.Auth.RetrofitClient
import com.example.main_project.Auth.ViewModels.ForgotPasswordViewModel
import com.example.main_project.databinding.FragmentForgotOtpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotOtp : Fragment() {

    private var _binding: FragmentForgotOtpBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var countdownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 30000
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotOtpBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            if (areAllDigitsEntered()) {
                val otp = getEnteredOtp()
                sharedViewModel.contact?.let { contact ->
                    sendOtpToApi(contact, otp,)
                }
                binding.loginBtn.isEnabled = false
                showLoadingDialog()
            } else {
                clearAllEntries()
                setEditTextErrorOutline()
                unfocusEditTexts()
            }
        }

        val email = sharedViewModel.contact
        binding.todoText.text = "Please enter the verification code we have sent to\n$email"

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.forgotOtp) {
                    findNavController().navigate(R.id.loginPage)
                }
            }
        })

        setUpOtpEditTexts()
        startTimer()

        return binding.root
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                binding.timer.text = "Didn’t receive any code?  Resend code"
                binding.timer.isEnabled = true
                binding.timer.setTextColor(resources.getColor(R.color.background, null))
                binding.timer.setOnClickListener {
                    showLoadingDialog()
                    resendOtp()
                }
            }
        }.start()
    }

    private fun updateTimerText() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        binding.timer.text = "Resend code in $seconds s"
        binding.timer.setTextColor(resources.getColor(R.color.background, null))
    }

    private fun resendOtp() {
        sharedViewModel.sendForgotPasswordRequest(
            onSuccess = {
                loadingDialog.dismiss()
                Toast.makeText(context, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                timeLeftInMillis = 60000
                startTimer()
            },
            onError = { errorMessage ->
                loadingDialog.dismiss()
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
        binding.timer.isEnabled = false
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

    private fun sendOtpToApi(contact: String, otp: String) {
        val request = ChangePasswordRequest(contact, otp)
        println(contact)
        println(otp)
        val call = RetrofitClient.instance.forgotPasswordOTP(request)

        call.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.newPassword)
                } else {
                    val errorMessage = response.body()?.message ?: "An error occurred. Please try again."
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    handleInvalidOtp()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                binding.loginBtn.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
    private fun handleInvalidOtp() {
        clearAllEntries()
        setEditTextErrorOutline()
        unfocusEditTexts()
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
                    if (!s.isNullOrEmpty() && index < editTexts.size - 1) {
                        editTexts[index + 1].requestFocus()
                        editTexts[index].setBackgroundResource(R.drawable.edittext_prop)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.action == android.view.KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        editTexts[index - 1].text.clear()
                        editTexts[index - 1].requestFocus()
                    } else if (editText.text.isNotEmpty()) {
                        editText.text.clear()
                    }
                    true
                } else {
                    false
                }
            }

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && editText.text.isNotEmpty() && index < editTexts.size - 1) {
                    editTexts[index + 1].requestFocus()
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
            editText.setBackgroundResource(R.drawable.error_prop)
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
        countdownTimer.cancel()
        _binding = null
    }
}
