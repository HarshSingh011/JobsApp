package com.example.main_project

import android.os.Bundle
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
import com.example.main_project.databinding.FragmentVerificationCodeBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationCode : Fragment() {

    private var _binding: FragmentVerificationCodeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: RegisterViewModel by activityViewModels()

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
            } else {
                clearAllEntries()
                setEditTextErrorOutline()
                unfocusEditTexts()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.verificationCode) {
                    findNavController().navigate(R.id.loginPage)
                }
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

    private fun sendOtpToApi(contact: String, otp: String) {
        val request = OtpRequest(contact = contact, otp = otp)
        val call = RetrofitClient.instance.validateOtp(request)

        call.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
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
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleInvalidOtp() {
        clearAllEntries()
        setEditTextErrorOutline()
        Toast.makeText(context, "Invalid OTP, please try again.", Toast.LENGTH_SHORT).show()
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
                    if (s != null && s.isNotEmpty()) {
                        editText.setBackgroundResource(R.drawable.edittext_prop)
                    }

                    if (s != null) {
                        if (s.length == 1 && index < editTexts.size - 1) {
                            editTexts[index + 1].requestFocus()
                        } else if (s.isEmpty() && index > 0) {
                            editTexts[index - 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { v, keyCode, _ ->
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
        _binding = null
    }
}
