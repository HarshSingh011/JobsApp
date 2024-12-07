package com.example.main_project.Auth.Fragments

import com.example.main_project.Auth.DataClasses.OtpRequest
import com.example.main_project.Auth.DataClasses.OtpResponse
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
import com.example.main_project.Auth.ViewModels.RegisterViewModel
import com.example.main_project.databinding.FragmentVerificationCodeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationCode : Fragment() {

    private var _binding: FragmentVerificationCodeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: RegisterViewModel by activityViewModels()
    private lateinit var countdownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 30000

    private lateinit var loadingDialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            if (areAllDigitsEntered()) {
                val otp = getEnteredOtp()
                sharedViewModel.email?.let { email ->
                    sendOtpToApi(email, otp)
                }
                showLoadingDialog()
                binding.loginBtn.isEnabled = false
            } else {
                clearAllEntries()
                setEditTextErrorOutline()
                unfocusEditTexts()
            }
        }

        val email = sharedViewModel.email
        binding.todoText.text = "Please enter the verification code we have sent to\n$email"

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.verificationCode) {
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
                binding.timer.text = "Didn’t receive any code?Resend code"
                binding.timer.isEnabled = true
                binding.timer.setTextColor(resources.getColor(R.color.background, null))
                binding.timer.setOnClickListener {
                    resendOtp()
                    showLoadingDialog()
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
        sharedViewModel.sendDataToApi(
            onSuccess = {
                loadingDialog.dismiss()
                Toast.makeText(context, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                timeLeftInMillis = 30000
                startTimer()
            },
            onError = {
                loadingDialog.dismiss()
                Toast.makeText(context,"OTP not send successfully", Toast.LENGTH_SHORT).show()
            }
        )
        binding.timer.isEnabled = false
    }

    private fun getEnteredOtp(): String {
        return binding.digitOne.text.toString() +
                binding.digitTwo.text.toString() +
                binding.digitThree.text.toString() +
                binding.digitFour.text.toString() +
                binding.digitFive.text.toString() +
                binding.digitSix.text.toString()
    }

    private fun sendOtpToApi(contact: String, otp: String) {
        val request = OtpRequest(contact = contact, otp = otp)
        val call = RetrofitClient.instance.validateOtp(request)

        call.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                loadingDialog.dismiss()
                binding.loginBtn.isEnabled = true
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.token?.let {
                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.verified)
                    } ?: run {
                        handleInvalidOtp()
                    }
                } else {
                    handleInvalidOtp()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                loadingDialog.dismiss()
                binding.loginBtn.isEnabled = true
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
