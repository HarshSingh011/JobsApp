package com.example.main_project.adapters

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.Recruiter.DataClasses.ApplicantApplication
import com.example.main_project.Recruiter.DataClasses.ApplicantContent
import com.example.main_project.Recruiter.DataClasses.UpdateStatusBody
import com.example.main_project.databinding.ApplicantBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class RecruiterApplicantsAdapter(
    private val applicantList: List<ApplicantApplication>,
    private val context: Context
) : RecyclerView.Adapter<RecruiterApplicantsAdapter.ApplicantViewHolder>() {

    inner class ApplicantViewHolder(val binding: ApplicantBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val binding = ApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        val applicantApplication = applicantList[position]
        val applicant = applicantApplication.applicant

        // Set skills
        holder.binding.Skills.text = applicant.skills.joinToString(", ")

        // Load resume
        val resumeUrl = applicant.resumeKey
        if (!resumeUrl.isNullOrEmpty()) {
            loadPdfThumbnail(resumeUrl, holder.binding.ResumeImage)

            holder.binding.ResumeImage.setOnClickListener {
                openFile(context, resumeUrl)
            }
        } else {
            holder.binding.ResumeImage.setImageResource(R.drawable.ic_menu_report_image)
        }

        // Handle Accept button
        holder.binding.Accept.setOnClickListener {
            val applicantId = applicantApplication.id
            val statusBody = UpdateStatusBody("Accepted")
            updateApplicationStatus(applicantId, statusBody)
        }

    }

    private fun loadProfileImage(imageUrl: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = downloadImage(imageUrl)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                    Log.e("RecruiterAdapter", "Error loading profile image: ${e.message}")
                }
            }
        }
    }

    private fun downloadImage(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun updateApplicationStatus(applicantId: Long, statusBody: UpdateStatusBody) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiClient = CandidateProfileRetrofitClient.instance(context)
                    .create(CandidateInterface::class.java)

                val response = apiClient.updateApplicationStatus(applicantId, statusBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Status updated successfully"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update application status", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("RecruiterAdapter", "Error updating status: ${e.message}")
                }
            }
        }
    }

    override fun getItemCount(): Int = applicantList.size

    private fun loadPdfThumbnail(pdfUrl: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = downloadFile(pdfUrl, imageView.context)
                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                val page = pdfRenderer.openPage(0)

                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                page.close()
                pdfRenderer.close()

                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                    Log.e("RecruiterAdapter", "Error loading PDF thumbnail: ${e.message}")
                }
            }
        }
    }

    private fun downloadFile(fileUrl: String, context: Context): File {
        val file = File(context.cacheDir, "temp_resume_${System.currentTimeMillis()}.pdf")
        URL(fileUrl).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun openFile(context: Context, fileUrl: String) {
        if (fileUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fileUrl)
                type = "application/pdf"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open the file", Toast.LENGTH_SHORT).show()
                Log.e("RecruiterAdapter", "Error opening file: ${e.message}")
            }
        } else {
            Toast.makeText(context, "Resume not available", Toast.LENGTH_SHORT).show()
        }
    }
}
