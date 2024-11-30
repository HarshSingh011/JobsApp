package com.example.main_project.candidate.Fragments

import android.app.Dialog
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
