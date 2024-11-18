package AuthPages

import AuthDataClasses.ForgotPasswordRequest
import AuthDataClasses.ForgotPasswordResponse
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_project.ForgotPasswordViewModel
import com.example.main_project.R
import com.example.main_project.RetrofitClient
import com.example.main_project.databinding.FragmentForgotPasswordBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.cnt.setOnClickListener {
            if (isInputValid()) {
                binding.cnt.isEnabled = false
                val username = getFormattedUsername()
                sharedViewModel.contact = username
                sendForgotPasswordRequest(username)
                showLoadingDialog()
            } else {
                applyErrorToEmailField()
            }
        }

        binding.editEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                resetEmailField()
            }
        }
    }

    private fun getFormattedUsername(): String {
        val input = binding.editEmail.editText?.text.toString().trim()
        return if (input.matches(Regex("^[0-9]{10}\$"))) {
            "+91$input"
        } else {
            input
        }
    }

    private fun isInputValid(): Boolean {
        val input = binding.editEmail.editText?.text.toString().trim()
        return input.isNotBlank()
    }

    private fun applyErrorToEmailField() {
        binding.editEmail.error = "Required"
        binding.editEmail.clearFocus()
        binding.editEmail.editText?.setBackgroundResource(R.drawable.error_prop)
    }

    private fun sendForgotPasswordRequest(username: String) {
        val request = ForgotPasswordRequest(contact = username)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                binding.cnt.isEnabled = true
                loadingDialog.dismiss()

                if (response.isSuccessful) {
                    response.body()?.let { forgotPasswordResponse ->
                        Toast.makeText(requireContext(), forgotPasswordResponse.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.forgotOtp)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorResponse ?: "Unknown error")
                    applyErrorToEmailField()
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("ForgotPasswordError", "Response code: ${response.code()} - $errorMessage")
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                loadingDialog.dismiss()
                binding.cnt.isEnabled = true
                applyErrorToEmailField()
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ForgotPasswordFailure", "Error: ${t.message}")
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

    private fun resetEmailField() {
        binding.editEmail.error = null
        binding.editEmail.editText?.setBackgroundResource(R.drawable.edittext_prop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
