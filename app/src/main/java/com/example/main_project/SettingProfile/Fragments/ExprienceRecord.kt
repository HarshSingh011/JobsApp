package com.example.main_project.SettingProfile.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.main_project.R
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.SettingProfile.DataClasses.Experience
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.databinding.FragmentExperienceBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.json.JSONObject

class ExprienceRecord : Fragment() {

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CandidateViewModel by activityViewModels()
    private lateinit var loadingDialog: Dialog

    override fun onResume() {
        super.onResume()
        val exp = resources.getStringArray(R.array.expri)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdownmenu, exp)
        binding.expDefine.setAdapter(arrayAdapter)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExperienceBinding.inflate(inflater, container, false)

        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.settingprofiledialog)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        loadingDialog.setCancelable(false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
            }
        })

        binding.exp.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.exp)
        }

        binding.companyName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.companyName)
        }

        binding.position.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.position)
        }

        binding.yearOfWork.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetToDefaultDrawable(binding.yearOfWork)
        }

        binding.submitButton.setOnClickListener {

        }

        binding.nextFragment.setOnClickListener {
            if (validateInputs()) {

            } else {
                Toast.makeText(requireContext(), "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        if(binding.expDefine.text.toString() == "NO") {
            setMenuVisibility(false)
            binding.position.visibility = View.GONE
            binding.companyName.visibility = View.GONE
            binding.yearOfWork.visibility = View.GONE
            binding.submitButton.visibility = View.GONE
        }
        return binding.root
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    private fun validateInputs(): Boolean {
        val companyName = binding.companyName.editText?.text.toString().trim()
        val position = binding.position.editText?.text.toString().trim()
        val yearOfWork = binding.yearOfWork.editText?.text.toString().trim()

        var isValid = true

        if (companyName.isBlank()) {
            setToErrorDrawable(binding.companyName)
            isValid = false
        }

        if (position.isBlank()) {
            setToErrorDrawable(binding.position)
            isValid = false
        }

        if (yearOfWork.isBlank()) {
            setToErrorDrawable(binding.yearOfWork)
            isValid = false
        }

        return isValid
    }

    private fun resetToDefaultDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.edittext_prop)
        editText.error = null
    }

    private fun setToErrorDrawable(editText: TextInputLayout) {
        editText.editText?.setBackgroundResource(R.drawable.error_prop)
        editText.error = "Empty Field!"
        editText.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}