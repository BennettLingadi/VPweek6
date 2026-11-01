package com.example.vpweek6

import kotlinx.serialization.Serializable

@Serializable
enum class ProjectStatus {
    IN_PROGRESS, COMPLETED
}

@Serializable
data class Community(
    val id: String,
    val name: String,
    val subtitle: String,
    val members: Int,
    val description: String,
    val joined: Boolean,
)

@Serializable
data class Project(
    val id: String,
    val communityId: String,
    val title: String,
    val description: String,
    val techStack: List<String>,
    val status: ProjectStatus = ProjectStatus.IN_PROGRESS,
)

val TECH_OPTIONS = listOf(
    "Kotlin", "Jetpack Compose", "Navigation", "Room", "Coroutines", "Hilt", "Firebase", "Python", "FastAPI", "React",
)
