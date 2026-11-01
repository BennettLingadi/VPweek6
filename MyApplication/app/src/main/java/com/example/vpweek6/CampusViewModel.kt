package com.example.vpweek6

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CampusState(
    val communities: List<Community> = listOf(
        Community("android", "Android Developer Club", "Mobile Development", 120, "Komunitas pengembang Android menggunakan Kotlin dan Jetpack Compose.", true),
        Community("web", "Web Engineering Club", "Fullstack & Frontend", 95, "Komunitas pengembang web modern dengan React, Kotlin Wasm, dan Node.js.", false),
        Community("ai", "AI & Data Science Lab", "Machine Learning", 150, "Eksplorasi kecerdasan buatan, LLM, dan Computer Vision.", true)
    ),
    val projects: List<Project> = listOf(
        Project("p1", "android", "Campus Connect", "Aplikasi mobile untuk menghubungkan mahasiswa dengan kegiatan kampus dan klub.", listOf("Jetpack Compose", "Kotlin", "Navigation"), ProjectStatus.IN_PROGRESS),
        Project("p2", "android", "Smart Library", "Sistem manajemen peminjaman buku perpustakaan berbasis QR Code.", listOf("Kotlin", "Room", "Coroutines"), ProjectStatus.COMPLETED),
        Project("p3", "ai", "AI Tutor Bot", "Chatbot asisten belajar mahasiswa menggunakan LLM API.", listOf("Python", "FastAPI", "OpenAI"), ProjectStatus.IN_PROGRESS)
    )
) {
    val joined: List<Community> get() = communities.filter { it.joined }

    fun community(id: String): Community? = communities.find { it.id == id }
    fun project(id: String): Project? = projects.find { it.id == id }
}

class CampusViewModel : ViewModel() {
    private val _state = MutableStateFlow(CampusState())
    val state: StateFlow<CampusState> = _state.asStateFlow()

    fun toggleJoin(communityId: String) {
        _state.update { current ->
            val updatedCommunities = current.communities.map { c ->
                if (c.id == communityId) c.copy(joined = !c.joined) else c
            }
            current.copy(communities = updatedCommunities)
        }
    }

    fun addProject(communityId: String, title: String, description: String, techStack: List<String>) {
        val newProj = Project(
            id = "p_${System.currentTimeMillis()}",
            communityId = communityId,
            title = title,
            description = description,
            techStack = techStack,
            status = ProjectStatus.IN_PROGRESS
        )
        _state.update { current ->
            current.copy(projects = listOf(newProj) + current.projects)
        }
    }

    fun deleteProject(projectId: String) {
        _state.update { current ->
            current.copy(projects = current.projects.filter { it.id != projectId })
        }
    }

    fun toggleStatus(projectId: String) {
        _state.update { current ->
            val updatedProjects = current.projects.map { p ->
                if (p.id == projectId) {
                    val newStatus = if (p.status == ProjectStatus.COMPLETED) ProjectStatus.IN_PROGRESS else ProjectStatus.COMPLETED
                    p.copy(status = newStatus)
                } else p
            }
            current.copy(projects = updatedProjects)
        }
    }
}
