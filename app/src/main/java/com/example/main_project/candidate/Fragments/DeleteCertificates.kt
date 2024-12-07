package com.example.main_project.candidate.Fragments

import android.app.Activity
import android.app.Dialog
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DeleteCertificates : Fragment() {

    private var _binding: FragmentDeleterCertificatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingDialog: Dialog

    private val candidateViewModel: CandidateProfileViewModel by activityViewModels()

    private var selectedFileUri: Uri? = null
    private var isUploadingCertificate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleterCertificatesBinding.inflate(inflater, container, false)

        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            updateUI(candidateData)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.candidateProfile)
            }
        })


        binding.delete.setOnClickListener {
            deleteResume()
        }

        binding.certificateadd.setOnClickListener {
            openFilePicker(true)
        }

        binding.resumeadd.setOnClickListener {
            openFilePicker(false)
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
            binding.resumeTextView.text = "Resume"
            binding.resumeImageView.setOnClickListener {
                openFile(requireContext(), resumeUrl)
            }
        }
    }

    private fun loadPdfThumbnail(pdfUrl: String, imageView: ImageView) {
        showLoadingDialog()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = downloadFile(pdfUrl)
                val bitmap = renderPdfPage(file)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                    loadingDialog.dismiss()
                }
            } catch (e: Exception) {
                Log.e("PDF_THUMBNAIL", "Error loading PDF thumbnail", e)
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun renderPdfPage(file: File): Bitmap {
        val pageRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        val page = pageRenderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pageRenderer.close()
        return bitmap
    }

    private fun downloadFile(fileUrl: String): File {
        val file = File(requireContext().cacheDir, "temp_file_${System.currentTimeMillis()}")
        val url = URL(fileUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Server returned HTTP ${connection.responseCode}")
        }

        connection.inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun openFile(context: Context, fileUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = downloadFile(fileUrl)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, getMimeType(fileUrl))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "Open File"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error opening file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getMimeType(url: String): String {
        return when {
            url.endsWith(".pdf", true) -> "application/pdf"
            url.endsWith(".jpg", true) || url.endsWith(".jpeg", true) -> "image/jpeg"
            url.endsWith(".png", true) -> "image/png"
            else -> "*/*"
        }
    }

    private fun showLoadingDialog() {
        if (!::loadingDialog.isInitialized) {
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

    private fun openFilePicker(isCertificate: Boolean) {
        isUploadingCertificate = isCertificate
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                selectedFileUri = uri
                if (isUploadingCertificate) {
                    uploadCertificate(uri)
                } else {
                    uploadResume(uri)
                }
            } else {
                Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadCertificate(uri: Uri) {
        val certificateName = getFileName(uri) ?: "Certificate"
        val certificateNameRequestBody = RequestBody.create("text/plain".toMediaType(), certificateName)
        val file = getFileFromUri(uri, certificateName)
        val certificateDataRequestBody = RequestBody.create("application/pdf".toMediaType(), file)
        val certificateDataPart = MultipartBody.Part.createFormData("certificateData", file.name, certificateDataRequestBody)

        lifecycleScope.launch {
            try {
                val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
                val response = apiService.uploadCertificate(certificateNameRequestBody, certificateDataPart)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Certificate uploaded successfully", Toast.LENGTH_SHORT).show()
                    fetchCurrentCandidate() // Refresh the data
                } else {
                    Toast.makeText(requireContext(), "Failed to upload certificate", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadResume(uri: Uri) {
        val file = getFileFromUri(uri, "resume.pdf")
        val resumeDataRequestBody = RequestBody.create("application/pdf".toMediaType(), file)
        val resumeDataPart = MultipartBody.Part.createFormData("Resume", file.name, resumeDataRequestBody)

        lifecycleScope.launch {
            try {
                val apiService = CandidateProfileRetrofitClient.instance(requireContext()).create(CandidateInterface::class.java)
                val response = apiService.uploadResume(resumeDataPart)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Resume uploaded successfully", Toast.LENGTH_SHORT).show()
                    fetchCurrentCandidate()
                } else {
                    Toast.makeText(requireContext(), "Failed to upload resume", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

    private fun getFileFromUri(uri: Uri, fileName: String): File {
        val file = File(requireContext().cacheDir, fileName)
        file.outputStream().use { outputStream ->
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    private fun fetchCurrentCandidate() {
        val apiService = CandidateProfileRetrofitClient.instance(requireContext())
            .create(CandidateInterface::class.java)
        showLoadingDialog()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getCurrentCandidate()
                if (response.isSuccessful && response.body() != null) {
                    loadingDialog.dismiss()
                    val candidateData = response.body() as CandidateDataGet
                    candidateViewModel.setCandidateData(candidateData)
                } else {
                    println("Error fetching data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                println("Exception in fetchCurrentCandidate: ${e.message}")
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

