package com.example.main_project.candidate.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.R
import com.example.main_project.candidate.DataClasses.Certificate
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.CertificatedeleteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class CertificateAdapter(
    private val certificates: MutableList<Certificate>,
    private val context: Context,
    private val viewModel: CandidateProfileViewModel
) : RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {

    inner class CertificateViewHolder(private val binding: CertificatedeleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(certificate: Certificate) {
            binding.CertificateName.text = certificate.certificateName

            val fileUrl = certificate.fileKey

            binding.delete.setOnClickListener {
                certificate.id?.let {
                    deleteCertificate(it)
                } ?: run {
                    Toast.makeText(context, "Invalid certificate ID", Toast.LENGTH_SHORT).show()
                }
            }

            if (fileUrl?.endsWith(".jpg") == true || fileUrl?.endsWith(".png") == true) {
                Glide.with(binding.root.context)
                    .load(fileUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background))
                    .into(binding.CertificateImage)

                binding.CertificateImage.setOnClickListener {
                    openFile(binding.root.context, fileUrl, "image/*")
                }
            } else if (fileUrl?.endsWith(".pdf") == true) {
                loadPdfThumbnail(fileUrl, binding.CertificateImage)

                binding.CertificateImage.setOnClickListener {
                    openFile(binding.root.context, fileUrl, "application/pdf")
                }
            } else {
                binding.CertificateImage.setImageResource(R.drawable.ic_launcher_background)
            }
        }

        private fun deleteCertificate(certificateId: String) {
            CoroutineScope(Dispatchers.Main).launch {  // Use CoroutineScope instead of lifecycleScope
                val retrofit = CandidateProfileRetrofitClient.instance(context)
                val apiService = retrofit.create(CandidateInterface::class.java)

                try {
                    val response = apiService.deleteCertificate(certificateId)

                    if (response.isSuccessful) {
                        // Successfully deleted the certificate, now remove it from the list
                        val position = certificates.indexOfFirst { it.id == certificateId }
                        if (position != -1) {
                            certificates.removeAt(position)
                            notifyItemRemoved(position)
                            Toast.makeText(context, "Certificate deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete certificate", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        imageView.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
            }
        }

        private fun downloadFile(fileUrl: String): File {
            val file = File(binding.root.context.cacheDir, "temp_certificate_${System.currentTimeMillis()}.pdf")
            URL(fileUrl).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
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
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val binding =
            CertificatedeleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CertificateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        holder.bind(certificates[position])
    }

    override fun getItemCount(): Int = certificates.size
}