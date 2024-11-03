package com.example.main_project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.main_project.databinding.FragmentForgotPasswordBinding

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        binding.cnt.setOnClickListener {
            validateInput()
        }

        binding.editEmail.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.editEmail.error = null
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.loginPage)
            }
        })

        return binding.root
    }

    private fun validateInput() {
        val username = binding.editEmail.editText?.text.toString()

        if (username.isBlank()) {
            binding.editEmail.error = "*Required"
        } else {
            sharedViewModel.contact = username
            sendForgotPasswordRequest(username)
        }
    }

    private fun sendForgotPasswordRequest(username: String) {
        val request = ForgotPasswordRequest(contact = username)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val forgotPasswordResponse = response.body()!!

                    Toast.makeText(requireContext(), forgotPasswordResponse.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.newPassword)
                } else {
                    binding.editEmail.error = "Invalid username"
                    Toast.makeText(requireContext(), "Invalid username", Toast.LENGTH_SHORT).show()
                    Log.e("ForgotPasswordError", "Response code: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ForgotPasswordFailure", "Error: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
