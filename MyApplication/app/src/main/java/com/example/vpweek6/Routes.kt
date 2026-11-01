package com.example.vpweek6

import kotlinx.serialization.Serializable

@Serializable object DashboardRoute
@Serializable object CommunitiesRoute
@Serializable data class ClubProjectsRoute(val communityId: String)
@Serializable data class ProjectDetailRoute(val projectId: String)
