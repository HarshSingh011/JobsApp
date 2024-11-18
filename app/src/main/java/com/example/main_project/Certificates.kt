package com.example.main_project

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.main_project.databinding.FragmentCertificatesBinding
import android.graphics.pdf.PdfRenderer
import java.io.FileDescriptor

class Certificates : Fragment() {

    private var _binding: FragmentCertificatesBinding? = null
    private val binding get() = _binding!!

    private var isCertificate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertificatesBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.yourProfile)
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

        // Navigate to the next fragment
        binding.nextFragment.setOnClickListener {
            findNavController().navigate(R.id.recuriterProfile1)
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
        } else {
            binding.resumePdf.text = fileName ?: "Resume uploaded"
        }
        if (isCertificate) {
            renderPdfPreview(uri, binding.certificatepdfPreview)
        } else {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

