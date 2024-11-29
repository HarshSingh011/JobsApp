package com.example.main_project.candidate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.candidate.Adapter.EducationEditAdapter
import com.example.main_project.candidate.Adapter.ExperienceEditAdapter
import com.example.main_project.candidate.Adapter.SkillEditAdapter
import com.example.main_project.candidate.DataClasses.*
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.FragmentCandidateEditDetailBinding
import kotlinx.coroutines.launch

class CandidateEditDetail : Fragment() {

    private var _binding: FragmentCandidateEditDetailBinding? = null
    private val binding get() = _binding!!

    private val candidateViewModel: CandidateProfileViewModel by activityViewModels()

    private lateinit var educationAdapter: EducationEditAdapter
    private lateinit var experienceAdapter: ExperienceEditAdapter
    private lateinit var skillAdapter: SkillEditAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCandidateEditDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        educationAdapter = EducationEditAdapter()
        experienceAdapter = ExperienceEditAdapter()
        skillAdapter = SkillEditAdapter()

        binding.recyclerViewEducation.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = educationAdapter
        }

        binding.recyclerViewExperience.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = experienceAdapter
        }

        binding.recyclerViewSkill.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = skillAdapter
        }
    }

    private fun observeViewModel() {
        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            if (candidateData != null) {
                updateUI(candidateData)
            } else {
                Toast.makeText(context, "No candidate data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(candidateData: CandidateDataGet) {
        val educationList = candidateData.education.map { education ->
            EducationShowDataClasses(
                IntituteName = education.institution?.trim() ?: "",
                Degree = education.degree?.trim() ?: "",
                DateOfCompletion = education.yearOfCompletion
            )
        }
        educationAdapter.submitList(educationList)

        val experienceList = candidateData.experience.map { experience ->
            ExperienceShowDataClasses(
                CompanyName = experience.companyName?.trim() ?: "",
                YearOfWork = experience.yearsWorked?.toString()?.trim() ?: "",
                Date = experience.position?.trim() ?: ""
            )
        }
        experienceAdapter.submitList(experienceList)

        val skillList = candidateData.skill.map { skill ->
            SkillShowDataClasses(skill = skill.trim())
        }
        skillAdapter.submitList(skillList)
    }

    private fun setupClickListeners() {
        binding.addEducation.setOnClickListener {
            val currentList = educationAdapter.currentList.toMutableList()
            currentList.add(EducationShowDataClasses("", "", null))
            educationAdapter.submitList(currentList)
        }

        binding.addExperience.setOnClickListener {
            val currentList = experienceAdapter.currentList.toMutableList()
            currentList.add(ExperienceShowDataClasses("", "", ""))
            experienceAdapter.submitList(currentList)
        }

        binding.buttonUpdate.setOnClickListener {
            val data = collectDataFromRecyclerViews()
            updateCandidateData(data)
        }
    }

    private fun collectDataFromRecyclerViews(): UpdateCandidateRequest {
        val educationList = (binding.recyclerViewEducation.adapter as EducationEditAdapter).getUpdatedList().map { education ->
            UpdateEducation(
                institution = education.IntituteName ?: "",
                degree = education.Degree ?: "",
                yearOfCompletion = education.DateOfCompletion ?: 0
            )
        }

        val experienceList = (binding.recyclerViewExperience.adapter as ExperienceEditAdapter).getUpdatedList().map { experience ->
            UpdateExperience(
                companyName = experience.CompanyName ?: "",
                yearsWorked = experience.YearOfWork?.toIntOrNull() ?: 0,
                position = experience.Date ?: ""
            )
        }

        val skillList = (binding.recyclerViewSkill.adapter as SkillEditAdapter).getUpdatedList().mapNotNull { it.skill }

        return UpdateCandidateRequest(
            education = educationList,
            experiences = experienceList,
            skill = skillList
        )
    }

    private fun updateCandidateData(data: UpdateCandidateRequest) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = CandidateProfileRetrofitClient.instance(requireContext())
                    .create(CandidateInterface::class.java)
                    .updateCandidate(data)

                if (response.isSuccessful) {
                    Toast.makeText(context, "Candidate updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update candidate: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println(e.message)
                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

