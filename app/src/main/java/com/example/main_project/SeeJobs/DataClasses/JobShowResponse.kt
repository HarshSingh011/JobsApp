package com.example.main_project.SeeJobs.DataClasses

data class JobShowResponse(
    val content: List<Job>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class JobApiResponse(
    val content: List<Job>,
    val pageable: Pageable,
    val totalPages: Int
)

data class Pageable(
    val totalPages: Int
)