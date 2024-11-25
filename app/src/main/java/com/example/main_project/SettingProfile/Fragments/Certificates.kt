package com.example.main_project.SettingProfile.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.main_project.R
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.CandidateInterface
import com.example.main_project.databinding.FragmentCertificatesBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class Certificates : Fragment() {

    private var _binding: FragmentCertificatesBinding? = null
    private val binding get() = _binding!!
    private var isApiSuccess = false

    private var isCertificate = true
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertificatesBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 1
            }
        })

        binding.certificate.setOnClickListener {
            isCertificate = true
            openFilePicker()
        }

        binding.resume.setOnClickListener {
            isCertificate = false
            openFilePicker()
        }

        binding.nextFragment.setOnClickListener {
            println("hello")
            findNavController().navigate(R.id.mainActivity4)
        }

        return binding.root
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val uri = intent?.data
                if (uri != null) {
                    selectedFileUri = uri
                    handleFileSelection(uri)
                } else {
                    Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun handleFileSelection(uri: Uri) {
        val fileName = getFileName(uri)
        if (isCertificate) {
            binding.certiPdf.text = fileName ?: "Certificate uploaded"
            renderPdfPreview(uri, binding.certificatepdfPreview)
            uploadCertificate(uri)
        } else {
            binding.resumePdf.text = fileName ?: "Resume uploaded"
            renderPdfPreview(uri, binding.resumepdfPreview)
            uploadResume(uri)
        }
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return uri.lastPathSegment
    }

    private fun renderPdfPreview(uri: Uri, imageView: ImageView) {
        val fileDescriptor: ParcelFileDescriptor? = requireContext().contentResolver.openFileDescriptor(uri, "r")
        if (fileDescriptor != null) {
            val pdfRenderer = PdfRenderer(fileDescriptor)

            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            imageView.setImageBitmap(bitmap)

            page.close()
            pdfRenderer.close()
        }
    }

    private fun uploadCertificate(uri: Uri) {
        val certificateName = binding.certiPdf.text.toString()

        val certificateNameRequestBody = RequestBody.create(
            "text/plain".toMediaType(), certificateName
        )

        val file = getFileFromUri(uri, "temp_certificate.pdf")

        val certificateDataRequestBody = RequestBody.create(
            "application/pdf".toMediaType(), file
        )
        val certificateDataPart = MultipartBody.Part.createFormData("certificateData", file.name, certificateDataRequestBody)
        println(certificateDataPart)

        lifecycleScope.launch {
            try {
                val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
                val response = apiService.uploadCertificate(certificateNameRequestBody, certificateDataPart)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Certificate uploaded successfully", Toast.LENGTH_SHORT).show()
                    isApiSuccess = true
                    restrictNavigation()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    println(errorMessage)
                    Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error Data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadResume(uri: Uri) {
        val file = getFileFromUri(uri, "temp_resume.pdf")

        val resumeDataRequestBody = RequestBody.create(
            "application/pdf".toMediaType(), file
        )
        val resumeDataPart = MultipartBody.Part.createFormData("Resume", file.name, resumeDataRequestBody)

        lifecycleScope.launch {
            try {
                val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
                val response = apiService.uploadResume(resumeDataPart)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Resume uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    println(errorMessage)
                    Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileFromUri(uri: Uri, fileName: String): File {
        val file = File(requireContext().cacheDir, fileName).apply {
            outputStream().use { outputStream ->
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    private fun restrictNavigation() {
        if (isApiSuccess) {
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            val tabLayout = requireActivity().findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)

            viewPager.isUserInputEnabled = false
            tabLayout.getTabAt(1)?.view?.isEnabled = false
            tabLayout.getTabAt(0)?.view?.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
