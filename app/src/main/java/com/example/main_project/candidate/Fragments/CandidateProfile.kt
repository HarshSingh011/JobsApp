package com.example.main_project.candidate.Fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.SettingProfile.DataClasses.Education
import com.example.main_project.SettingProfile.DataClasses.Experience
import com.example.main_project.candidate.Adapter.CertificateShowAdapter
import com.example.main_project.candidate.Adapter.EducationShowAdapter
import com.example.main_project.candidate.Adapter.ExperienceShowAdapter
import com.example.main_project.candidate.Adapter.SkillShowAdapter
import com.example.main_project.candidate.DataClasses.CandidateDataGet
import com.example.main_project.candidate.DataClasses.Certificate
import com.example.main_project.candidate.DataClasses.EducationShowDataClasses
import com.example.main_project.candidate.DataClasses.ExperienceShowDataClasses
import com.example.main_project.candidate.DataClasses.SkillShowDataClasses
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.FragmentCandidateProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class CandidateProfile : Fragment() {

    private var _binding: FragmentCandidateProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingDialog: Dialog

    private val candidateViewModel: CandidateProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCandidateProfileBinding.inflate(inflater, container, false)

        setupRecyclerViews(emptyList(), emptyList(), emptyList(), emptyList())

        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            println("CandidateProfile observed data: $candidateData")
            updateUI(candidateData)
        }

        binding.profileImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        fetchCurrentCandidate()

        binding.editdetails.setOnClickListener {
            findNavController().navigate(R.id.candidateEditDetail)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.mainActivity2)
            }
        })


        binding.editdocuments.setOnClickListener(){
            findNavController().navigate(R.id.deleteCertificates)
        }

        return binding.root
    }

    private fun setupRecyclerViews(
        educationList: List<Education>,
        experienceList: List<Experience>,
        skillList: List<SkillShowDataClasses>,
        certificateList: List<Certificate>
    ) {
        val educationShowAdapter = EducationShowAdapter(
            educationList.map { EducationShowDataClasses(it.institution, it.degree, it.yearOfCompletion) }
        )
        binding.EducationDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = educationShowAdapter
        }

        val experienceShowAdapter = ExperienceShowAdapter(
            experienceList.map { ExperienceShowDataClasses(it.companyName, it.yearsWorked.toString(), it.position) }
        )
        binding.ExperienceDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = experienceShowAdapter
        }

        val skillShowAdapter = SkillShowAdapter(skillList)
        binding.SkillDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = skillShowAdapter
        }

        val certificateShowAdapter = CertificateShowAdapter(certificateList)
        binding.CertificateDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = certificateShowAdapter
        }
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
                    println("Fetched candidate data: $candidateData")
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

    private fun updateUI(candidateData: CandidateDataGet) {
        binding.firstname.text = candidateData.firstName ?: "N/A"
        binding.lastname.text = candidateData.lastName ?: "N/A"
        binding.Email.text = candidateData.email ?: "N/A"
        binding.phonenumber.text = candidateData.phone ?: "N/A"
        binding.Dob.text = candidateData.dob ?: "N/A"

        val profileImageUrl = candidateData.profileImageKey ?: ""
        handleProfileImage(profileImageUrl)

        val resumeUrl = candidateData.resumeKey ?: ""
        handleResume(resumeUrl)

        setupRecyclerViews(
            candidateData.education ?: emptyList(),
            candidateData.experience ?: emptyList(),
            candidateData.skill.map { SkillShowDataClasses(it) },
            candidateData.certificate ?: emptyList()
        )
    }

    private fun handleProfileImage(profileImageUrl: String) {
        if (profileImageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.profileImageView)
        } else {
            binding.profileImageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun handleResume(resumeUrl: String?) {
        if (resumeUrl.isNullOrEmpty()) {
            binding.resumeImageView.setImageResource(R.drawable.ic_launcher_background)
        } else {
            loadPdfThumbnail(resumeUrl, binding.resumeImageView)

            binding.resumeImageView.setOnClickListener {
                openResume(resumeUrl)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileUri = data?.data
        if (fileUri != null) {
            binding.profileImageView.setImageURI(fileUri)
            binding.profileImageView.setContentPadding(0, 0, 0, 0)
            uploadImage(fileUri)
        } else {
            Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPdfThumbnail(pdfUrl: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = downloadFile(pdfUrl)

                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                val page = pdfRenderer.openPage(0)

                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                page.close()
                pdfRenderer.close()

                CoroutineScope(Dispatchers.Main).launch {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                Log.e("PDF Load Error", "Failed to load PDF preview: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }
    }

    private fun downloadFile(fileUrl: String): File {
        val file = File(requireContext().cacheDir, "temp_resume.pdf")
        URL(fileUrl).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun uploadImage(fileUri: Uri) {
        val context = requireContext()
        showLoadingDialog()
        lifecycleScope.launch {
            try {
                loadingDialog.dismiss()
                Log.d("ProfilePictureUpload", "Original URI: $fileUri")
                Log.d("ProfilePictureUpload", "URI Scheme: ${fileUri.scheme}")
                Log.d("ProfilePictureUpload", "URI Path: ${fileUri.path}")

                val filePath = getActualFilePath(fileUri)

                if (filePath == null) {
                    Toast.makeText(context, "Unable to retrieve file path", Toast.LENGTH_LONG).show()
                    Log.e("ProfilePictureUpload", "File path retrieval failed")
                    return@launch
                }

                val file = File(filePath)

                Log.d("ProfilePictureUpload", "Resolved File Path: ${file.absolutePath}")
                Log.d("ProfilePictureUpload", "File Exists: ${file.exists()}")
                Log.d("ProfilePictureUpload", "File Length: ${file.length()} bytes")

                if (!file.exists()) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(fileUri) ?: "image/jpeg"

                Log.d("ProfilePictureUpload", "Detected MIME Type: $mimeType")

                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val api = CandidateProfileRetrofitClient.instance(context).create(CandidateInterface::class.java)
                val response = api.uploadProfilePicture(body)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Image uploaded successfully!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    Log.d("ProfilePictureUpload", "Response: $message")
                } else {
                    loadingDialog.dismiss()
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse).optString("message", "Failed to upload image")
                    } catch (e: Exception) {
                        "Failed to upload image"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("ProfilePictureUpload", "Error Response Code: ${response.code()}")
                    Log.e("ProfilePictureUpload", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("API Error", "Exception details", e)
            }
        }
    }

    private fun getActualFilePath(uri: Uri): String? {
        val context = requireContext()
        return when {
            "content" == uri.scheme -> {
                try {
                    val projection = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = context.contentResolver.query(uri, projection, null, null, null)

                    cursor?.use {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        it.moveToFirst()
                        it.getString(columnIndex)
                    }
                } catch (e: Exception) {
                    uriToFileWithContentResolver(uri)
                }
            }

            "file" == uri.scheme -> uri.path

            else -> {
                Log.e("FileUpload", "Unsupported URI scheme: ${uri.scheme}")
                null
            }
        }
    }

    private fun uriToFileWithContentResolver(uri: Uri): String? {
        val context = requireContext()
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile.absolutePath
        } catch (e: Exception) {
            Log.e("FileUpload", "Error converting URI to file: ${e.message}")
            return null
        }
    }

    private fun openResume(resumeUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(resumeUrl)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}