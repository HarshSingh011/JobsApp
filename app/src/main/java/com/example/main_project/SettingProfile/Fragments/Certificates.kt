package com.example.main_project.SettingProfile.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_project.R
import com.example.main_project.SettingProfile.CandidateProfileRetrofitClient
import com.example.main_project.SettingProfile.CandidateInterface
import com.example.main_project.databinding.FragmentCertificatesBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import android.view.ViewGroup as ViewGroup1

class Certificates : Fragment() {

    private var _binding: FragmentCertificatesBinding? = null
    private val binding get() = _binding!!

    private var isCertificate = true
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup1?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertificatesBinding.inflate(inflater, container, false)

        // Handle back press to navigate to your profile
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
            }
        })

        // Set listeners for certificate and resume
        binding.certificate.setOnClickListener {
            isCertificate = true
            openFilePicker()
        }

        binding.resume.setOnClickListener {
            isCertificate = false
            openFilePicker()
        }

        binding.nextFragment.setOnClickListener {
            if (selectedFileUri != null) {
                uploadCertificate()
            } else {
                Toast.makeText(requireContext(), "Please select a file", Toast.LENGTH_SHORT).show()
            }
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
        } else {
            binding.resumePdf.text = fileName ?: "Resume uploaded"
            renderPdfPreview(uri, binding.resumepdfPreview)
        }
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
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

    private fun uploadCertificate() {
        selectedFileUri?.let { uri ->
            val certificateName = binding.certiPdf.text.toString()

            val certificateNameRequestBody = RequestBody.create(
                "text/plain".toMediaType(), certificateName
            )

            val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            val file = File(requireContext().cacheDir, "temp_certificate.pdf").apply {
                outputStream().use { outputStream ->
                    fileDescriptor?.fileDescriptor?.let { descriptor ->
                        FileInputStream(descriptor).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }

            val certificateDataRequestBody = RequestBody.create(
                "application/pdf".toMediaType(), file
            )
            val certificateDataPart = MultipartBody.Part.createFormData("certificateData", file.name, certificateDataRequestBody)

            lifecycleScope.launch {
                try {
                    val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
                    val response = apiService.uploadCertificate(certificateNameRequestBody, certificateDataPart)

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Certificate uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}