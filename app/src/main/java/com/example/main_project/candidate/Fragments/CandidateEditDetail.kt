package com.example.main_project.candidate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.SettingProfile.ViewModels.CandidateViewModel
import com.example.main_project.candidate.Adapter.EducationEditAdapter
import com.example.main_project.candidate.Adapter.ExperienceEditAdapter
import com.example.main_project.candidate.Adapter.SkillEditAdapter
import com.example.main_project.candidate.DataClasses.CandidateDataGet
import com.example.main_project.candidate.DataClasses.EducationShowDataClasses
import com.example.main_project.candidate.DataClasses.ExperienceShowDataClasses
import com.example.main_project.candidate.DataClasses.SkillShowDataClasses
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.FragmentCandidateEditDetailBinding

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
        observeViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("CandidateEditDetail: onViewCreated called")

        println("candidate data from viewmodel: ${candidateViewModel.candidateData.value}")

        setupRecyclerViews()

        observeViewModel()
    }

    private fun setupRecyclerViews() {
        educationAdapter = EducationEditAdapter()
        experienceAdapter = ExperienceEditAdapter()
        skillAdapter = SkillEditAdapter()

        binding.recyclerViewEducation.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = educationAdapter
        }

        binding.recyclerViewExperience.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            adapter = experienceAdapter
        }

        binding.recyclerViewSkill.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            adapter = skillAdapter
        }
    }

    private fun observeViewModel() {
        println("Starting to observe ViewModel in CandidateEditDetail")
        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            if (candidateData != null) {
                println("Observed Candidate Data: $candidateData")
                updateUI(candidateData)
            } else {
                println("No Candidate Data observed!")
            }
        }
        println(candidateViewModel.candidateData.value)
    }

    private fun updateUI(candidateData: CandidateDataGet) {
        println("updateUI called with candidateData: $candidateData")
        println(candidateData.education)
        candidateData.education?.let { educationList ->
            val mappedEducationList = educationList.map { education ->
                EducationShowDataClasses(
                    IntituteName = education.institution ?: "Unknown",
                    Degree = education.degree ?: "Unknown",
                    DateOfCompletion = education.yearOfCompletion
                )
            }
            println("Mapped Education List: $mappedEducationList")
            educationAdapter.submitList(mappedEducationList)
            educationAdapter.notifyDataSetChanged()
        } ?: println("updateUI: Education list is null or empty")

        candidateData.experience?.let { experienceList ->
            val mappedExperienceList = experienceList.map { experience ->
                ExperienceShowDataClasses(
                    CompanyName = experience.companyName ?: "Unknown",
                    YearOfWork = experience.yearsWorked?.toString() ?: "Unknown",
                    Date = experience.position ?: "Unknown"
                )
            }
            println("Mapped Experience List: $mappedExperienceList")
            experienceAdapter.submitList(mappedExperienceList)
            experienceAdapter.notifyDataSetChanged()
        } ?: println("updateUI: Experience list is null or empty")

        candidateData.skill?.let { skillList ->
            val mappedSkillList = skillList.map { skill ->
                SkillShowDataClasses(skill = skill ?: "Unknown")
            }
            println("Mapped Skill List: $mappedSkillList")
            skillAdapter.submitList(mappedSkillList)
            skillAdapter.notifyDataSetChanged()
        } ?: println("updateUI: Skill list is null or empty")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
