package com.example.main_project.candidate.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.candidate.Adapter.CertificateAdapter
import com.example.main_project.candidate.DataClasses.CandidateDataGet
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.FragmentDeleterCertificatesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DeleteCertificates : Fragment() {

    private var _binding: FragmentDeleterCertificatesBinding? = null
    private val binding get() = _binding!!

    private val candidateViewModel: CandidateProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleterCertificatesBinding.inflate(inflater, container, false)

        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            updateUI(candidateData)
        }

        binding.delete.setOnClickListener {
            deleteResume()
        }

        return binding.root
    }

    private fun updateUI(candidateData: CandidateDataGet) {
        val certificateAdapter = CertificateAdapter(
            candidateData.certificate?.toMutableList() ?: mutableListOf(),
            requireContext(),
            candidateViewModel
        )
        binding.CertificateDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = certificateAdapter
        }

        val resumeUrl = candidateData.resumeKey ?: ""
        handleResume(resumeUrl)
    }

    private fun handleResume(resumeUrl: String?) {
        if (resumeUrl.isNullOrEmpty()) {
            binding.resumeImageView.setImageResource(R.drawable.ic_launcher_background)
            binding.resumeTextView.text = "No Resume"
        } else {
            loadPdfThumbnail(resumeUrl, binding.resumeImageView)
            binding.resumeImageView.setOnClickListener {
                openFile(requireContext(), resumeUrl, "application/pdf")
            }
        }
    }

    private fun loadPdfThumbnail(pdfUrl: String, imageView: ImageView) {
        lifecycleScope.launch {
            try {
                val file = downloadFile(pdfUrl)
                if (file.exists()) {
                    val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                    val page = pdfRenderer.openPage(0)

                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    page.close()
                    pdfRenderer.close()

                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                    }
                } else {
                    Log.e("PDF Load Error", "File not found: $pdfUrl")
                    withContext(Dispatchers.Main) {
                        imageView.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
            } catch (e: Exception) {
                Log.e("PDF Load Error", "Failed to load PDF preview: ${e.message}")
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }
    }

    private fun downloadFile(fileUrl: String): File {
        val file = File(requireContext().cacheDir, "temp_resume.pdf")
        try {
            URL(fileUrl).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.e("File Download", "Error downloading file: ${e.message}")
        }
        return file
    }

    private fun openFile(context: Context, fileUrl: String, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(fileUrl)
            type = mimeType
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteResume() {
        lifecycleScope.launch {
            val retrofit = CandidateProfileRetrofitClient.instance(requireContext())
            val apiService = retrofit.create(CandidateInterface::class.java)

            try {
                val response = apiService.deleteResume()

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Resume deleted successfully", Toast.LENGTH_SHORT).show()
                    binding.resumeTextView.text = "No Resume"
                    binding.resumeImageView.setImageResource(R.drawable.ic_launcher_background)
                    binding.resumeImageView.setOnClickListener(null)
                } else {
                    Toast.makeText(requireContext(), "Failed to delete resume", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

